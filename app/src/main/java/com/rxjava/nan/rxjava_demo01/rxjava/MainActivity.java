package com.rxjava.nan.rxjava_demo01.rxjava;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.rxjava.nan.rxjava_demo01.R;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

/**
 * 给初学者的RxJava2.0教程  教程一
 *
 * @author Nan
 * @see <a href="http://www.jianshu.com/u/c50b715ccaeb">给初学者的RxJava2.0教程</a>
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViewListener();
    }

    private void initViewListener() {
        findViewById(R.id.rxJava01).setOnClickListener(this);
        findViewById(R.id.rxJava02).setOnClickListener(this);
        findViewById(R.id.rxJava03).setOnClickListener(this);
        findViewById(R.id.rxJava04).setOnClickListener(this);
        findViewById(R.id.rxJava05).setOnClickListener(this);
        findViewById(R.id.rxJava06).setOnClickListener(this);
        findViewById(R.id.rxJava07).setOnClickListener(this);
        findViewById(R.id.rxJava08).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rxJava01:
                /* 测试1：事件发送的顺序是先1,后2,后3这样的顺序, 事件接收的顺序也是先1,后2,后3的顺序
                  测试 integer */
                rxJava_test_01();
                break;
            case R.id.rxJava02:
                /*测试2：事件发送的顺序是先1,后2,后3这样的顺序, 事件接收的顺序也是先1,后2,后3的顺序
                  测试 String */
                rxJava_test_02();
                break;
            case R.id.rxJava03:
                /* 测试3：当上游发送了一个onError后, 上游onError之后的事件将继续发送,
                  而下游收到onError事件之后将不再继续接收事件 */
                rxJava_test_03();
                break;
            case R.id.rxJava04:
                /* 测试4：当上游发送了一个onComplete后, 上游onComplete之后的事件将会继续发送,
                  而下游收到onComplete事件之后将不再继续接收事件 */
                rxJava_test_04();
                break;
            case R.id.rxJava05:
                /* 测试5：发射多个onError后  第二个会报错 */
                rxJava_test_05();
                break;
            case R.id.rxJava06:
                /* 测试6, 我们让上游依次发送1,2,3,complete,4,在下游收到第二个事件之后, 切断水管 */
                rxJava_test_06();
                break;
            case R.id.rxJava07:
                /*subscribe()有多个重载的方法 */
                rxJava_test_07();
                break;
            case R.id.rxJava08:
                /*给初学者的RxJava2.0教程(二)*/
                intentRxJava02();
                break;
        }
    }

    /**
     * 给初学者的RxJava2.0教程(二)
     */
    private void intentRxJava02() {
        Intent intent = new Intent(MainActivity.this, RxJava02Activity.class);
        startActivity(intent);
    }

    /**
     * 两根水管通过一定的方式连接起来，使得上游每产生一个事件，
     * 下游就能收到该事件。注意这里和官网的事件图是反过来的,
     * 这里的事件发送的顺序是先1,后2,后3这样的顺序, 事件接收的顺序也是先1,后2,后3的顺序,
     * 我觉得这样更符合我们普通人的思维, 简单明了.
     * <p>
     * 这里的上游和下游就分别对应着RxJava中的Observable和Observer，
     * 它们之间的连接就对应着subscribe()，因此这个关系用RxJava来表示就是：
     * <p>
     * <p>
     * 测试1：事件发送的顺序是先1,后2,后3这样的顺序, 事件接收的顺序也是先1,后2,后3的顺序
     * 测试 integer
     */
    public void rxJava_test_01() {
        Observable<Integer> integerObservable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
                emitter.onNext(4);
                emitter.onComplete();
            }
        });

        Observer<Integer> observer = new Observer<Integer>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                Log.d(TAG, "onSubscribe");
            }

            @Override
            public void onNext(@NonNull Integer integer) {
                Log.d(TAG, "onNext" + integer);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.d(TAG, "onError");
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete");
            }
        };

        integerObservable.subscribe(observer);

    }

    /**
     * 测试2：事件发送的顺序是先1,后2,后3这样的顺序, 事件接收的顺序也是先1,后2,后3的顺序
     * 测试 String
     */
    public void rxJava_test_02() {
        Observable.create(new ObservableOnSubscribe<String>() {

            /*Emitter发射器*/
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                e.onNext("1.1");
                e.onNext("2.2");
                e.onNext("3.3");
                e.onComplete();
            }
        }).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                Log.d(TAG, "onSubscribe" + d);
            }

            @Override
            public void onNext(@NonNull String s) {
                Log.d(TAG, "onNext" + s);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.d(TAG, "onError" + e.toString());
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete");
            }
        });


    }

    /**
     * 测试3：当上游发送了一个onError后, 上游onError之后的事件将继续发送,
     * 而下游收到onError事件之后将不再继续接收事件.
     */
    public void rxJava_test_03() {
        Observable.create(new ObservableOnSubscribe<String>() {

            /*Emitter发射器*/
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                e.onNext("1.1");
                e.onNext("2.2");
                /*
                 * 当上游发送了一个onError后, 上游onError之后的事件将继续发送,
                  * 而下游收到onError事件之后将不再继续接收事件.
                 */
                e.onError(new Throwable("mua~"));
                e.onNext("3.3");
                e.onComplete();
            }
        }).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                Log.d(TAG, "onSubscribe" + d);
            }

            @Override
            public void onNext(@NonNull String s) {
                Log.d(TAG, "onNext" + s);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.d(TAG, "onError" + e.toString());
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete");
            }
        });

    }

    /**
     * 测试4：当上游发送了一个onComplete后, 上游onComplete之后的事件将会继续发送,
     * 而下游收到onComplete事件之后将不再继续接收事件
     */
    public void rxJava_test_04() {
        Observable.create(new ObservableOnSubscribe<String>() {

            /*Emitter发射器*/
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                e.onNext("1.1");
                e.onNext("2.2");
                /* 当上游发送了一个onComplete后, 上游onComplete之后的事件将会继续发送,
                 而下游收到onComplete事件之后将不再继续接收事件*/
                e.onComplete();
                e.onNext("3.3");
            }
        }).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                Log.d(TAG, "onSubscribe" + d);
            }

            @Override
            public void onNext(@NonNull String s) {
                Log.d(TAG, "onNext" + s);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.d(TAG, "onError" + e.toString());
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete");
            }
        });
    }

    /**
     * 测试5：发射多个onError后  第二个会报错
     */
    public void rxJava_test_05() {
        Observable.create(new ObservableOnSubscribe<String>() {

            /*Emitter发射器*/
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                e.onNext("1.1");
                e.onNext("2.2");
                //e.onError(new Throwable("00"));
                e.onNext("3.3");
                //第二个onError会报错：io.reactivex.exceptions.UndeliverableException: java.lang.Throwable: 0_0
                //e.onError(new Throwable("0_0"));
            }
        }).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                Log.d(TAG, "onSubscribe" + d);
            }

            @Override
            public void onNext(@NonNull String s) {
                Log.d(TAG, "onNext" + s);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.d(TAG, "onError" + e.toString());
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete");
            }
        });
    }


    /**
     * 它理解成两根管道之间的一个机关, 当调用它的dispose()方法时, 它就会将两根管道切断, 从而导致下游收不到事件.
     * 注意: 调用dispose()并不会导致上游不再继续发送事件, 上游会继续发送剩余的事件.
     * <p>
     * <p>
     * 测试6, 我们让上游依次发送1,2,3,complete,4,在下游收到第二个事件之后, 切断水管
     */
    public void rxJava_test_06() {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                Log.d(TAG, "emit 1");
                e.onNext("1");
                Log.d(TAG, "emit 2");
                e.onNext("2");
                Log.d(TAG, "emit 3");
                e.onNext("3");
                Log.d(TAG, "emit onComplete");
//                e.onComplete();
                Log.d(TAG, "emit 4");
                e.onNext("4");
            }
        }).subscribe(new Observer<String>() {

            private Disposable disposable;
            private int i;

            @Override
            public void onSubscribe(@NonNull Disposable d) {
                Log.d(TAG, "subscribe");
                disposable = d;
            }

            @Override
            public void onNext(@NonNull String s) {
                Log.d(TAG, "onNext: " + s);
                i++;
                if (i == 1) {
                    Log.d(TAG, "dispose");
                    //调用dispose()并不会导致上游不再继续发送事件, 上游会继续发送剩余的事件.
                    disposable.dispose();
                    Log.d(TAG, "isDisposed : " + disposable.isDisposed());
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.d(TAG, "error");
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete");
            }
        });
    }


    /**
     * subscribe()有多个重载的方法:
     * public final Disposable subscribe() {}
     * public final Disposable subscribe(Consumer<? super T> onNext) {}
     * <p>
     * public final Disposable subscribe(Consumer<? super T> onNext, Consumer<? super Throwable> onError) {}
     * <p>
     * public final Disposable subscribe(Consumer<? super T> onNext, Consumer<? super Throwable> onError, Action onComplete) {}
     * <p>
     * public final Disposable subscribe(Consumer<? super T> onNext, Consumer<? super Throwable> onError,
     * Action onComplete, Consumer<? super Disposable> onSubscribe) {}
     * <p>
     * public final void subscribe(Observer<? super T> observer) {}
     * <p>
     * 1.不带任何参数的subscribe() 表示下游不关心任何事件,你上游尽管发你的数据去吧, 老子可不管你发什么.
     */
    public void rxJava_test_07() {
        //1.不带任何参数的subscribe() 表示下游不关心任何事件,你上游尽管发你的数据去吧, 老子可不管你发什么.
        //Observable.create(new ObservableOnSubscribe<String>() {
//            @Override
//            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
//
//            }
//        }).subscribe();


        //2.带有一个Consumer参数的方法表示下游只关心onNext事件,
        // 其他的事件我假装没看见, 因此我们如果只需要onNext事件可以这么写:
//        Observable.create(new ObservableOnSubscribe<String>() {
//            @Override
//            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
//                e.onNext("1");
//                e.onNext("2");
//                io.reactivex.exceptions.OnErrorNotImplementedException: mua~
//                 e.onError(new Throwable("mua~"));  //如果没有Consumer<Throwable>()会抛出异常
//                e.onNext("3");
//                e.onComplete();  //执行Complete后下游不再接收
//                e.onNext("4");
//            }
//        }).subscribe(new Consumer<String>() {
//            @Override
//            public void accept(@NonNull String s) throws Exception {
//                Log.d(TAG, "accept-->" + s);
//            }
//        });


        //3.接收onError onComplete onDisposable
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                e.onNext("1");
                e.onNext("2");
                //io.reactivex.exceptions.OnErrorNotImplementedException: mua~
                e.onError(new Throwable("mua~"));  //会抛出异常
                e.onNext("3");
                e.onComplete();  //执行Complete后下游不再接收
                e.onNext("4");
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(@NonNull String s) throws Exception {
                Log.d(TAG, "accept-->" + s);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(@NonNull Throwable throwable) throws Exception {
                Log.d(TAG, "Throwable-->" + throwable.toString());
            }
        }, new Action() {
            @Override
            public void run() throws Exception {
                Log.d(TAG, "Action");
            }
        }, new Consumer<Disposable>() {
            @Override
            public void accept(@NonNull Disposable disposable) throws Exception {
                Log.d(TAG, "Disposable-->" + disposable.isDisposed());
            }
        });
    }


}
