package com.ykbjson.lib.mmvp;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;
import android.util.Log;

import com.ykbjson.lib.mmvp.annotation.ActionProcess;
import com.ykbjson.lib.mmvp.annotation.MMVPActionProcessor;
import com.ykbjson.lib.mmvp.internal.BindPresenter;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 包名：com.ykbjson.lib.mmvp
 * 描述：View和Presenter层交互处理器
 * 创建者：yankebin
 * 日期：2018/4/12
 */
public final class MMVPArtist {
    private static final String TAG = "MMVPArtist";

    private static final int FLAG_HANDLE_ACTION = 100000;
    /**
     * 注册View缓存
     */
    @VisibleForTesting
    static final List<MMVPView> VIEW_CACHE = new LinkedList<>();
    /**
     * View和Presenter关系缓存
     */
    @VisibleForTesting
    static final Map<Class<?>, List<MMVPPresenter>> VIEW_PRESENTERS_CACHE = new LinkedHashMap<>();

    /**
     * 注册了{@link ActionProcess}注解的方法缓存
     */
    @VisibleForTesting
    static final Map<Class<?>, Map<String, Method>> METHODS_CACHE = new LinkedHashMap<>();


    /**
     * 注册了{@link MMVPActionProcessor}注解的类的构造方法缓存
     */
    @VisibleForTesting
    static final Map<Class<?>, Constructor<? extends IMMVPActionHandler>> EXECUTORS = new LinkedHashMap<>();


    private static boolean enableLog = true;

    private static boolean useApt = true;

    @SuppressLint("HandlerLeak")
    private static Handler dispatchActionHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case FLAG_HANDLE_ACTION:
                    MMVPAction action = (MMVPAction) msg.obj;
                    handleAction(action);
                    break;
            }
        }
    };

    private MMVPArtist() {
        throw new AssertionError("No instances.");
    }

    /**
     * 日志开关
     *
     * @param enableLog
     */
    public static void setEnableLog(boolean enableLog) {
        MMVPArtist.enableLog = enableLog;
    }

    /**
     * apt开关
     *
     * @param useApt
     */
    public static void setUseApt(boolean useApt) {
        MMVPArtist.useApt = useApt;
    }

    /**
     * 创建 {@link MMVPAction}
     *
     * @param sourceClass 创建Action的class
     * @param targetClass 接收Action的class
     * @param action      需要执行的操作
     * @return {@link MMVPAction}
     */
    public static MMVPAction buildAction(Class<?> sourceClass, Class<?> targetClass, String action) {
        MMVPActionDescription actionContent = new MMVPActionDescription();
        actionContent.setAction(action);
        return new MMVPAction(sourceClass, targetClass).setAction(actionContent);
    }

    /**
     * 发送HVPAction
     *
     * @param action {@link MMVPAction}
     */
    static void sendAction(@NonNull MMVPAction action) {
        sendAction(action, 0);
    }

    /**
     * 发送HVPAction
     *
     * @param action     {@link MMVPAction}
     * @param delayMills 延时毫秒数
     */
    static void sendAction(@NonNull MMVPAction action, long delayMills) {
        if (null == action.getAction()
                || TextUtils.isEmpty(action.getAction().getAction())
                || null == action.getSourceClass()
                || null == action.getTargetClass()) {
            throw new IllegalArgumentException("Invalid MMVPAction");
        }
        Message message = dispatchActionHandler.obtainMessage(FLAG_HANDLE_ACTION, action);
        dispatchActionHandler.sendMessageDelayed(message, delayMills);
    }


    /**
     * 处理HVPAction
     *
     * @param action {@link MMVPAction}
     */
    private static void handleAction(@NonNull MMVPAction action) {
        Class<?> sourceClass = action.getSourceClass();
        if (MMVPPresenter.class.isAssignableFrom(sourceClass)) {
            handleActionFromPresenter(action);
        } else if (MMVPView.class.isAssignableFrom(sourceClass)) {
            handleActionFromView(action);
        } else {
            throw new IllegalArgumentException("Invalid class type of the MMVPAction's targetClass and sourceClass");
        }
    }

    /**
     * 处理Presenter发送来的Action
     *
     * @param action {@link MMVPAction}
     */
    private static void handleActionFromPresenter(MMVPAction action) {
        if (VIEW_CACHE.isEmpty()) {
            if (enableLog) {
                Log.d(TAG, " Can not find the MMVPAction's targetClass [ " +
                        action.getTargetClass().getName() + " ],because the VIEW_CACHE is empty");
            }
            return;
        }
        IMMVPActionHandler find = null;
        for (MMVPView hvpView : VIEW_CACHE) {
            if (hvpView.getClass().equals(action.getTargetClass())) {
                find = hvpView;
                break;
            }
        }
        if (null == find) {
            if (enableLog) {
                Log.w(TAG, " Can not find the MMVPAction's targetClass [ " +
                        action.getTargetClass().getName() + " ] , it is not registered or has been destroyed ");
            }
            return;
        }
        if (!(useApt ? executeByApt(action, find) : execute(action, find))) {
            find.handleAction(action);
        }
    }

    /**
     * 处理View发送来的Action
     *
     * @param action {@link MMVPAction}
     */
    private static void handleActionFromView(MMVPAction action) {
        if (!VIEW_PRESENTERS_CACHE.containsKey(action.getSourceClass())) {
            if (enableLog) {
                Log.w(TAG, " The MMVPAction's sourceClass [ " + action.getSourceClass().getName() +
                        " ]  is not registered  or has been destroyed ");
            }
            return;
        }
        List<MMVPPresenter> hvpPresenterList = VIEW_PRESENTERS_CACHE.get(action.getSourceClass());
        if (null == hvpPresenterList || hvpPresenterList.isEmpty()) {
            if (enableLog) {
                Log.w(TAG, "PresenterList is empty ,have you ever add annotation BindPresenter" +
                        " for this view [ " + action.getSourceClass().getName() + " ] ?");
            }
            return;
        }
        IMMVPActionHandler find = null;
        for (MMVPPresenter hvpPresenter : hvpPresenterList) {
            if (action.getTargetClass().equals(hvpPresenter.getClass())) {
                find = hvpPresenter;
                break;
            }
        }

        if (null == find) {
            if (enableLog) {
                Log.w(TAG, " Can not find the MMVPAction's targetClass [ "
                        + action.getTargetClass().getName() + " ] ");
            }
            return;
        }
        if (!(useApt ? executeByApt(action, find) : execute(action, find))) {
            find.handleAction(action);
        }
    }

    /**
     * 执行action里目标类需要执行的方法
     *
     * @param action {@link MMVPAction}
     * @param find   {@link MMVPView}或{@link MMVPPresenter}
     * @return
     */
    private static boolean execute(MMVPAction action, IMMVPActionHandler find) {
        Method executeMethod = findRegisterMMVPActionMethod(action);
        if (null == executeMethod) {
            if (enableLog) {
                Log.d(TAG, " Find " + find.getClass().getName() + "'s execute method failure");
            }
            return false;
        }
        if (enableLog) {
            Log.d(TAG, " Find  method " + find.getClass().getName() + "." + executeMethod.getName() + " success");
        }

        List<Object> paramList = new ArrayList<>();
        ActionProcess methodAnnotation = executeMethod.getAnnotation(ActionProcess.class);
        if (methodAnnotation.needActionParam()) {
            if (methodAnnotation.needTransformAction()) {
                action = action.transform();
            }
            paramList.add(action);
        }
        if (methodAnnotation.needActionParams() && null != action.getParams() && !action.getParams().isEmpty()) {
            for (String key : action.getParams().keySet()) {
                paramList.add(action.getParam(key));
            }
        }
        Object[] params = paramList.isEmpty() ? null : paramList.toArray();
        try {
            executeMethod.setAccessible(true);
            executeMethod.invoke(find, params);
            if (enableLog) {
                Log.d(TAG, " Execute "
                        + find.getClass().getName() + "." + executeMethod.getName() + " success");
            }
            return true;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            if (enableLog) {
                Log.d(TAG, " Execute "
                        + action.getTargetClass().getName() + "." + executeMethod.getName() + " failure", e);
            }
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            if (enableLog) {
                Log.d(TAG, " Execute "
                        + action.getTargetClass().getName() + "." + executeMethod.getName() + " failure", e);
            }
        }

        return false;
    }

    /**
     * 注册View
     *
     * @param view {@link MMVPView}
     */
    @UiThread
    public static void registerView(@NonNull MMVPView view) {
        if (VIEW_CACHE.contains(view)) {
            if (enableLog) {
                Log.w(TAG, " ReRegister [ " + view.getClass().getName() + " ]");
            }
            return;
        }
        BindPresenter annotation = view.getClass().getAnnotation(BindPresenter.class);
        if (null == annotation) {
            throw new IllegalArgumentException("Can not find the annotation : BindPresenter, for [ "
                    + view.getClass().getName() + " ] ");
        }
        Class<? extends MMVPPresenter> presenterClasses[] = annotation.value();
        if (presenterClasses.length < 1) {
            throw new IllegalArgumentException(" Invalid presenter size for [ " + view.getClass().getName() + " ]");
        }
        final LinkedList<MMVPPresenter> presenterLinkedList = new LinkedList<>();
        for (Class<? extends MMVPPresenter> presenterClass : annotation.value()) {
            try {
                MMVPPresenter presenter = presenterClass.newInstance();
                presenterLinkedList.add(presenter);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        VIEW_CACHE.add(view);
        VIEW_PRESENTERS_CACHE.put(view.getClass(), presenterLinkedList);
        if (enableLog) {
            Log.d(TAG, " RegisterView [ " + view.getClass().getName() + " ]");
        }
    }


    /**
     * 注销View
     *
     * @param view {@link MMVPView}
     */
    @UiThread
    public static void unregisterView(@NonNull MMVPView view) {
        VIEW_CACHE.remove(view);
        for (Class<?> clazz : VIEW_PRESENTERS_CACHE.keySet()) {
            List<MMVPPresenter> presenterList = VIEW_PRESENTERS_CACHE.get(clazz);
            if (null == presenterList || presenterList.isEmpty()) {
                continue;
            }
            for (MMVPPresenter presenter : presenterList) {
                METHODS_CACHE.remove(presenter.getClass());
            }
        }
        METHODS_CACHE.remove(view.getClass());
        VIEW_PRESENTERS_CACHE.remove(view.getClass());
        if (enableLog) {
            Log.d(TAG, " unregisterView [ " + view.getClass().getName() + " ]");
        }
    }


    /**
     * 获取某个Presenter
     *
     * @param viewClass      当前viewClass
     * @param presenterClass 需要的presenterClass
     * @param <T>            需要的presenter
     * @return 需要的presenter
     */
    @Nullable
    public static <T extends MMVPPresenter> T getPresenter(@NonNull Class<? extends MMVPView> viewClass,
                                                           @NonNull Class<? extends MMVPPresenter> presenterClass) {
        List<MMVPPresenter> presenterList = VIEW_PRESENTERS_CACHE.get(viewClass);
        if (null == presenterList || presenterList.isEmpty()) {
            return (T) null;
        }
        for (MMVPPresenter presenter : presenterList) {
            if (presenterClass.equals(presenter.getClass())) {
                return (T) presenter;
            }
        }
        return (T) null;
    }


    /**
     * 找到某个类里注册了{@link ActionProcess}的方法,该方法注册的action为传入的{@link MMVPAction}里的action
     *
     * @param action {@link MMVPAction}
     * @return
     */
    private static Method findRegisterMMVPActionMethod(@NonNull MMVPAction action) {
        final Class<?> targetClass = action.getTargetClass();
        final String methodKey = String.format("%s_$$_$$_%s", targetClass.getCanonicalName(),
                action.getAction().getAction());
        Map<String, Method> methodMap = METHODS_CACHE.get(targetClass);
        Method executeMethod = null;
        if (null != methodMap && !methodMap.isEmpty()) {
            executeMethod = methodMap.get(methodKey);
        }
        if (null == executeMethod) {
            Method[] methods = targetClass.getMethods();
            for (Method method : methods) {
                ActionProcess methodAnnotation = method.getAnnotation(ActionProcess.class);
                if (null == methodAnnotation) {
                    continue;
                }
                if (!TextUtils.equals(methodAnnotation.value(), action.getAction().getAction())) {
                    continue;
                }
                executeMethod = method;
                break;
            }
            if (null != executeMethod) {
                if (null == methodMap) {
                    methodMap = new LinkedHashMap<>();
                    METHODS_CACHE.put(targetClass, methodMap);
                }
                methodMap.put(methodKey, executeMethod);
            }
        }
        return executeMethod;
    }
    //-----------------------------2018-05-09新增，APT方式替代反射方式-----------------------------------

    /**
     * 执行action里目标类需要执行的方法
     *
     * @param action {@link MMVPAction}
     * @param target 要执行action的类
     * @return
     */
    private static boolean executeByApt(@NonNull MMVPAction action, @NonNull Object target) {
        IMMVPActionHandler executor = createExecutor(target);
        return executor.handleAction(action);
    }


    /**
     * 创建{@link IMMVPActionHandler}
     *
     * @param target 当前关联的目标对象
     * @return
     */
    private static IMMVPActionHandler createExecutor(@NonNull Object target) {
        Class<?> targetClass = target.getClass();
        if (enableLog) {
            Log.d(TAG, "Looking up executor for " + targetClass.getName());
        }
        Constructor<? extends IMMVPActionHandler> constructor = findExecutorConstructorForClass(targetClass);

        if (constructor == null) {
            return IMMVPActionHandler.EMPTY;
        }

        //noinspection TryWithIdenticalCatches Resolves to API 19+ only type.
        try {
            return constructor.newInstance(target);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Unable to invoke " + constructor, e);
        } catch (InstantiationException e) {
            throw new RuntimeException("Unable to invoke " + constructor, e);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }
            if (cause instanceof Error) {
                throw (Error) cause;
            }
            throw new RuntimeException("Unable to create executor instance.", cause);
        }
    }


    /**
     * 创建{@link IMMVPActionHandler}
     *
     * @param cls 当前关联的目标类
     * @return
     */
    @Nullable
    @CheckResult
    @UiThread
    private static Constructor<? extends IMMVPActionHandler> findExecutorConstructorForClass(Class<?> cls) {
        Constructor<? extends IMMVPActionHandler> executorCtor = EXECUTORS.get(cls);
        if (executorCtor != null) {
            if (enableLog) {
                Log.d(TAG, "Cached in executor map.");
            }
            return executorCtor;
        }
        String clsName = cls.getName();
        if (clsName.startsWith("android.") || clsName.startsWith("java.")) {
            if (enableLog) {
                Log.d(TAG, "MISS: Reached framework class. Abandoning search.");
            }
            return null;
        }
        try {
            Class<?> executorClass = cls.getClassLoader().loadClass(clsName + "_MMVPActionProcessor");
            //noinspection unchecked
            executorCtor = (Constructor<? extends IMMVPActionHandler>) executorClass.getConstructor(cls);
            if (enableLog) {
                Log.d(TAG, "HIT: Loaded executor class and constructor.");
            }
        } catch (ClassNotFoundException e) {
            if (enableLog) {
                Log.d(TAG, "Not found. Trying superclass " + cls.getSuperclass().getName());
            }
            executorCtor = findExecutorConstructorForClass(cls.getSuperclass());
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Unable to find executor constructor for " + clsName, e);
        }
        EXECUTORS.put(cls, executorCtor);
        return executorCtor;
    }
}
