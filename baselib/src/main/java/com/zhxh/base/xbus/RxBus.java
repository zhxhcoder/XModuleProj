package com.zhxh.base.xbus;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * Created by zhxh on 2018/8/24
 */
public class RxBus {

    private static volatile RxBus defaultInstance;

    private Map<Class, List<Disposable>> subscriptionsByEventType = new HashMap<>();

    private Map<Object, List<Class>> eventTypesBySubscriber = new HashMap<>();

    private Map<Class, List<SubscriberMethod>> subscriberMethodByEventType = new HashMap<>();

    /*stick数据*/
    private final Map<Class<?>, Object> stickyEvent = new ConcurrentHashMap<>();
    // 主题
    private final Subject bus;

    public RxBus() {
        bus = PublishSubject.create().toSerialized();
    }

    // 单例RxBus
    public static RxBus getDefault() {
        RxBus rxBus = defaultInstance;
        if (defaultInstance == null) {
            synchronized (RxBus.class) {
                rxBus = defaultInstance;
                if (defaultInstance == null) {
                    rxBus = new RxBus();
                    defaultInstance = rxBus;
                }
            }
        }
        return rxBus;
    }

    /**
     * 提供了一个新的事件,单一类型
     *
     * @param o 事件数据
     */
    public void post(Object o) {
        synchronized (stickyEvent) {
            stickyEvent.put(o.getClass(), o);
        }
        bus.onNext(new Msg(-1, o));
    }

    /**
     * 提供了一个新的事件,根据code进行分发
     *
     * @param code 事件code
     * @param o
     */
    public void post(int code, Object o) {
        bus.onNext(new Msg(code, o));
    }

    public void postSticky(int code, Object o) {
        synchronized (stickyEvent) {
            stickyEvent.put(o.getClass(), o);
        }
        bus.onNext(new Msg(code, o));
    }


    /**
     * 根据传递的code和 eventType 类型返回特定类型(eventType)的 被观察者
     *
     * @param code      事件code
     * @param eventType 事件类型
     * @param <T>
     * @return
     */
    public <T> Observable<T> toObservable(final int code, final Class<T> eventType) {
        return bus.ofType(Msg.class)
                .filter(new Predicate<Msg>() {
                    @Override
                    public boolean test(Msg o) throws Exception {
                        return o.getCode() == code && eventType.isInstance(o.getObject());
                    }
                }).map(new Function<Msg, Object>() {
                    @Override
                    public Object apply(Msg o) throws Exception {
                        return o.getObject();
                    }
                }).cast(eventType);
    }


    /**
     * 根据传递的 eventType 类型返回特定类型(eventType)的 被观察者
     */
    public <T> Observable<T> toObservableSticky(final Class<T> eventType) {
        synchronized (stickyEvent) {
            Observable<T> observable = bus.ofType(eventType);
            final Object event = stickyEvent.get(eventType);

            if (event != null) {
                return observable.mergeWith(Observable.create(new ObservableOnSubscribe<T>() {
                    @Override
                    public void subscribe(ObservableEmitter<T> subscriber) throws Exception {
                        subscriber.onNext(eventType.cast(event));
                    }
                }));
            } else {
                return observable;
            }
        }
    }


    /**
     * 注册
     *
     * @param subscriber 订阅者
     */
    public void register(Object subscriber) {
        /*避免重复创建*/
        if (eventTypesBySubscriber.containsKey(subscriber)) {
            return;
        }
        Class<?> subClass = subscriber.getClass();
        Method[] methods = subClass.getDeclaredMethods();
        //boolean recive = false;
        for (Method method : methods) {
            if (method.isAnnotationPresent(Subscribe.class)) {
                //    recive = true;
                //获得参数类型
                Class[] parameterType = method.getParameterTypes();
                //参数不为空 且参数个数为1
                if (parameterType != null && parameterType.length == 1) {

                    Class eventType = parameterType[0];

                    addEventTypeToMap(subscriber, eventType);
                    Subscribe sub = method.getAnnotation(Subscribe.class);
                    int code = sub.code();
                    ThreadMode threadMode = sub.threadMode();
                    boolean sticky = sub.sticky();

                    SubscriberMethod subscriberMethod = new SubscriberMethod(subscriber, method, eventType, code, threadMode,
                            sticky);

                    if (isAdd(eventType, subscriberMethod)) {
                        addSubscriber(subscriberMethod);
                    }
                    addSubscriberToMap(eventType, subscriberMethod);
                }
            }
        }
        /*没有接受对象，抛出异常*/
//        if (!receiver) {
//            throw new RuntimeException("RxBus error:no receiver target event");
//        }
    }


    /**
     * 检查是否已经添加过sub事件
     *
     * @param eventType
     * @param subscriberMethod
     * @return
     */
    private boolean isAdd(Class eventType, SubscriberMethod subscriberMethod) {
        boolean result = true;
        List<SubscriberMethod> subscriberMethods = subscriberMethodByEventType.get(eventType);
        if (subscriberMethods != null && subscriberMethods.size() > 0) {
            for (SubscriberMethod subscriberMethod1 : subscriberMethods) {
                if (subscriberMethod1.code == subscriberMethod.code) {
                    result = false;
                    break;
                }
            }
        }
        return result;
    }


    /**
     * 将event的类型以订阅中subscriber为key保存到map里
     *
     * @param subscriber 订阅者
     * @param eventType  event类型
     */
    private void addEventTypeToMap(Object subscriber, Class eventType) {
        List<Class> eventTypes = eventTypesBySubscriber.get(subscriber);
        if (eventTypes == null) {
            eventTypes = new ArrayList<>();
            eventTypesBySubscriber.put(subscriber, eventTypes);
        }

        if (!eventTypes.contains(eventType)) {
            eventTypes.add(eventType);
        }
    }

    /**
     * 将注解方法信息以event类型为key保存到map中
     *
     * @param eventType        event类型
     * @param subscriberMethod 注解方法信息
     */
    private void addSubscriberToMap(Class eventType, SubscriberMethod subscriberMethod) {
        List<SubscriberMethod> subscriberMethods = subscriberMethodByEventType.get(eventType);
        if (subscriberMethods == null) {
            subscriberMethods = new ArrayList<>();
            subscriberMethodByEventType.put(eventType, subscriberMethods);
        }

        if (!subscriberMethods.contains(subscriberMethod)) {
            subscriberMethods.add(subscriberMethod);
        }
    }


    /**
     * 将订阅事件以event类型为key保存到map,用于取消订阅时用
     *
     * @param subscriber   subscriber对象类
     * @param subscription 订阅事件
     */
    private void addSubscriptionToMap(Class subscriber, Disposable subscription) {
        List<Disposable> subscriptions = subscriptionsByEventType.get(subscriber);
        if (subscriptions == null) {
            subscriptions = new ArrayList<>();
            subscriptionsByEventType.put(subscriber, subscriptions);
        }

        if (!subscriptions.contains(subscription)) {
            subscriptions.add(subscription);
        }
    }


    /**
     * 用RxJava添加订阅者
     *
     * @param subscriberMethod
     */
    public void addSubscriber(final SubscriberMethod subscriberMethod) {
        Observable observable;
        if (subscriberMethod.sticky) {
            observable = toObservableSticky(subscriberMethod.eventType);
        } else {
            observable = toObservable(subscriberMethod.code, subscriberMethod.eventType);
        }
        Disposable subscription = postToObservable(observable, subscriberMethod)
                .subscribe(new Consumer() {
                    @Override
                    public void accept(Object o) throws Exception {
                        callEvent(subscriberMethod.code, o);
                    }
                });
        addSubscriptionToMap(subscriberMethod.subscriber.getClass(), subscription);
    }


    /**
     * 用于处理订阅事件在那个线程中执行
     *
     * @param observable
     * @param subscriberMethod
     * @return
     */
    private Observable postToObservable(Observable observable, SubscriberMethod subscriberMethod) {

        switch (subscriberMethod.threadMode) {
            case MAIN_THREAD:
                observable.observeOn(AndroidSchedulers.mainThread());
                break;
            case IO:
                observable.observeOn(Schedulers.io());
                break;
            case NEW_THREAD:
                observable.observeOn(Schedulers.newThread());
                break;
            case COMPUTATION:
                observable.observeOn(Schedulers.computation());
                break;
            case TRAMPOLINE:
                observable.observeOn(Schedulers.trampoline());
                break;
            default:
                throw new IllegalStateException("Unknown thread mode: " + subscriberMethod.threadMode);
        }
        return observable;
    }


    /**
     * 回调到订阅者的方法中
     *
     * @param code   code
     * @param object obj
     */
    private void callEvent(int code, Object object) {
        Class eventClass = object.getClass();
        List<SubscriberMethod> methods = subscriberMethodByEventType.get(eventClass);
        if (methods != null && methods.size() > 0) {
            for (SubscriberMethod subscriberMethod : methods) {
                Subscribe sub = subscriberMethod.method.getAnnotation(Subscribe.class);
                int c = sub.code();
                if (c == code) {
                    subscriberMethod.invoke(object);
                }

            }
        }
    }


    /**
     * 取消注册
     *
     * @param subscriber
     */
    public void unRegister(Object subscriber) {
        List<Class> subscribedTypes = eventTypesBySubscriber.get(subscriber);
        if (subscribedTypes != null) {
            for (Class<?> eventType : subscribedTypes) {
                unSubscribeBySubscriber(subscriber.getClass());
                unSubscribeMethodByEventType(subscriber, eventType);
            }
            eventTypesBySubscriber.remove(subscriber);
        }
    }


    /**
     * subscriptions unsubscribe
     *
     * @param subscriber
     */
    private void unSubscribeBySubscriber(Class subscriber) {
        List<Disposable> subscriptions = subscriptionsByEventType.get(subscriber);
        if (subscriptions != null) {
            Iterator<Disposable> iterator = subscriptions.iterator();
            while (iterator.hasNext()) {
                Disposable subscription = iterator.next();
                if (subscription != null && !subscription.isDisposed()) {
                    subscription.dispose();
                    iterator.remove();
                }
            }
        }
    }

    /**
     * 移除subscriber对应的subscriberMethods
     *
     * @param subscriber
     * @param eventType
     */
    private void unSubscribeMethodByEventType(Object subscriber, Class eventType) {
        List<SubscriberMethod> subscriberMethods = subscriberMethodByEventType.get(eventType);
        if (subscriberMethods != null && subscriberMethods.size() > 0) {
            Iterator<SubscriberMethod> iterator = subscriberMethods.iterator();
            while (iterator.hasNext()) {
                SubscriberMethod subscriberMethod = iterator.next();
                if (subscriberMethod.subscriber.equals(subscriber)) {
                    iterator.remove();
                }
            }
        }
    }


    /**
     * 移除指定eventType的Sticky事件
     */
    public <T> T removeStickyEvent(Class<T> eventType) {
        synchronized (stickyEvent) {
            return eventType.cast(stickyEvent.remove(eventType));
        }
    }

    /**
     * 移除所有的Sticky事件
     */
    public void removeAllStickyEvents() {
        synchronized (stickyEvent) {
            stickyEvent.clear();
        }
    }

    public class Msg {
        private int code;
        private Object object;

        Msg(int code, Object o) {
            this.code = code;
            this.object = o;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public Object getObject() {
            return object;
        }

        public void setObject(Object object) {
            this.object = object;
        }
    }

    public class SubscriberMethod {

        public Method method;
        public ThreadMode threadMode;
        public Class<?> eventType;
        public Object subscriber;
        public int code;
        public boolean sticky;

        SubscriberMethod(Object subscriber, Method method, Class<?> eventType, int code, ThreadMode threadMode, boolean sticky) {
            this.method = method;
            this.threadMode = threadMode;
            this.eventType = eventType;
            this.subscriber = subscriber;
            this.code = code;
            this.sticky = sticky;
        }

        /**
         * 调用方法
         *
         * @param o 参数
         */
        public void invoke(Object o) {
            try {
                method.invoke(subscriber, o);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

    }
}
