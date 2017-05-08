package com.rxjava.nan.rxjava_demo01.rxjava;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.rxjava.nan.rxjava_demo01.R;
import com.rxjava.nan.rxjava_demo01.network.Api;
import com.rxjava.nan.rxjava_demo01.network.RetrofitNetWork;
import com.rxjava.nan.rxjava_demo01.request.UserBaseInfoResponse;
import com.rxjava.nan.rxjava_demo01.request.UserExtraInfoResponse;
import com.rxjava.nan.rxjava_demo01.request.UserInfo;

import java.io.InterruptedIOException;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;


/**
 * 给初学者的RxJava2.0教程(四)
 */
public class RxJava04Activity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx_java04);
        initViewListener();
    }

    private void initViewListener() {
        findViewById(R.id.rxJava01).setOnClickListener(this);
        findViewById(R.id.rxJava02).setOnClickListener(this);
        findViewById(R.id.rxJava03).setOnClickListener(this);
        findViewById(R.id.rxJava04).setOnClickListener(this);
        findViewById(R.id.rxJava08).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rxJava01:
                rxJava_test_01();
                break;
            case R.id.rxJava02:
                rxJava_test_02();
                break;
            case R.id.rxJava03:
                rxJava_test_03();
                break;
            case R.id.rxJava04:
                rxJava_test_04();
                break;
            case R.id.rxJava08:
                intentRxJava05();
                break;
        }
    }


    /**
     * Zip通过一个函数将多个Observable发送的事件结合到一起，然后发送这些组合到一起的事件.
     * 它按照严格的顺序应用这个函数。它只发射与发射数据项最少的那个Observable一样多的数据。
     */
    private void rxJava_test_01() {
        Observable<Integer> observable1 = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> e) throws Exception {
                Log.d(TAG, "emitter 1");
                e.onNext(1);
                Log.d(TAG, "emitter 2");
                e.onNext(2);
                Log.d(TAG, "emitter 3");
                e.onNext(3);
                Log.d(TAG, "emitter 4");
                e.onNext(4);
                Log.d(TAG, "emitter onComplete");
                e.onComplete();
            }
        });

        Observable<String> observable2 = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                Log.d(TAG, "emitter s 1");
                e.onNext("a");
                Log.d(TAG, "emitter s 2");
                e.onNext("b");
                Log.d(TAG, "emitter s 3");
                e.onNext("c");
                Log.d(TAG, "emitter s 4");
                e.onNext("d");
                Log.d(TAG, "emitter s onComplete");
                e.onComplete();
            }
        });

        Observable.zip(observable1, observable2, new BiFunction<Integer, String, String>() {
            @Override
            public String apply(@NonNull Integer integer, @NonNull String s) throws Exception {
                return integer + s;
            }
        }).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                Log.d(TAG, "onSubscribe");
            }

            @Override
            public void onNext(@NonNull String s) {
                Log.d(TAG, "onNext-->" + s);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.d(TAG, "onError");
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete");
            }
        });


        /*
        * log 输出
        * com.rxjava.nan.rxjava_demo01 D/RxJava04Activity: onSubscribe
        * com.rxjava.nan.rxjava_demo01 D/RxJava04Activity: emitter 1
        * com.rxjava.nan.rxjava_demo01 D/RxJava04Activity: emitter 2
        * com.rxjava.nan.rxjava_demo01 D/RxJava04Activity: emitter 3
        * com.rxjava.nan.rxjava_demo01 D/RxJava04Activity: emitter 4
        * com.rxjava.nan.rxjava_demo01 D/RxJava04Activity: emitter onComplete
        * com.rxjava.nan.rxjava_demo01 D/RxJava04Activity: emitter s 1
        * com.rxjava.nan.rxjava_demo01 D/RxJava04Activity: onNext-->1a
        * com.rxjava.nan.rxjava_demo01 D/RxJava04Activity: emitter s 2
        * com.rxjava.nan.rxjava_demo01 D/RxJava04Activity: onNext-->2b
        * com.rxjava.nan.rxjava_demo01 D/RxJava04Activity: emitter s 3
        * com.rxjava.nan.rxjava_demo01 D/RxJava04Activity: onNext-->3c
        * com.rxjava.nan.rxjava_demo01 D/RxJava04Activity: emitter s 4
        * com.rxjava.nan.rxjava_demo01 D/RxJava04Activity: onNext-->4d
        * com.rxjava.nan.rxjava_demo01 D/RxJava04Activity: onComplete
        * com.rxjava.nan.rxjava_demo01 D/RxJava04Activity: emitter s onComplete
        *
        * */
    }


    /**
     * 一发送完了之后, 二才开始发送啊? 到底是不是呢, 我们来验证一下:
     */
    private void rxJava_test_02() {
        Observable<Integer> observable1 = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> e) throws Exception {
                Log.d(TAG, "emitter 1");
                e.onNext(1);
                Thread.sleep(1000);

                Log.d(TAG, "emitter 2");
                e.onNext(2);
                Thread.sleep(1000);

                Log.d(TAG, "emitter 3");
                e.onNext(3);
                Thread.sleep(1000);

                Log.d(TAG, "emitter 4");
                e.onNext(4);
                Thread.sleep(1000);

                Log.d(TAG, "emitter onComplete");
                e.onComplete();
            }
        });

        Observable<String> observable2 = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                Log.d(TAG, "emitter s 1");
                e.onNext("a");
                Thread.sleep(1000);

                Log.d(TAG, "emitter s 2");
                e.onNext("b");
                Thread.sleep(1000);

                Log.d(TAG, "emitter s 3");
                e.onNext("c");
                Thread.sleep(1000);

                Log.d(TAG, "emitter s 4");
                e.onNext("d");
                Thread.sleep(1000);

                Log.d(TAG, "emitter s onComplete");
                e.onComplete();
            }
        });

        Observable.zip(observable1, observable2, new BiFunction<Integer, String, String>() {
            @Override
            public String apply(@NonNull Integer integer, @NonNull String s) throws Exception {
                return integer + s;
            }
        }).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                Log.d(TAG, "onSubscribe");
            }

            @Override
            public void onNext(@NonNull String s) {
                Log.d(TAG, "onNext-->" + s);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.d(TAG, "onError");
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete");
            }
        });


        /*
        * log 输出
        * com.rxjava.nan.rxjava_demo01 D/RxJava04Activity: onSubscribe
        * com.rxjava.nan.rxjava_demo01 D/RxJava04Activity: emitter 1
        * com.rxjava.nan.rxjava_demo01 D/RxJava04Activity: emitter 2
        * com.rxjava.nan.rxjava_demo01 D/RxJava04Activity: emitter 3
        * com.rxjava.nan.rxjava_demo01 D/RxJava04Activity: emitter 4
        * com.rxjava.nan.rxjava_demo01 D/RxJava04Activity: emitter onComplete
        * com.rxjava.nan.rxjava_demo01 D/RxJava04Activity: emitter s 1
        * com.rxjava.nan.rxjava_demo01 D/RxJava04Activity: onNext-->1a
        * com.rxjava.nan.rxjava_demo01 D/RxJava04Activity: emitter s 2
        * com.rxjava.nan.rxjava_demo01 D/RxJava04Activity: onNext-->2b
        * com.rxjava.nan.rxjava_demo01 D/RxJava04Activity: emitter s 3
        * com.rxjava.nan.rxjava_demo01 D/RxJava04Activity: onNext-->3c
        * com.rxjava.nan.rxjava_demo01 D/RxJava04Activity: emitter s 4
        * com.rxjava.nan.rxjava_demo01 D/RxJava04Activity: onNext-->4d
        * com.rxjava.nan.rxjava_demo01 D/RxJava04Activity: onComplete
        * com.rxjava.nan.rxjava_demo01 D/RxJava04Activity: emitter s onComplete
        *
        * */
    }


    /**
     * 在同一个线程中执行  看看执行顺序
     */
    private void rxJava_test_03() {
        Observable<Integer> observable1 = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> e) throws Exception {
                Log.d(TAG, "emitter 1");
                e.onNext(1);
                Thread.sleep(1000);

                Log.d(TAG, "emitter 2");
                e.onNext(2);
                Thread.sleep(1000);

                Log.d(TAG, "emitter 3");
                e.onNext(3);
                Thread.sleep(1000);

                Log.d(TAG, "emitter 4");
                e.onNext(4);
                Thread.sleep(1000);

                Log.d(TAG, "emitter onComplete");
                e.onComplete();

            }
        }).subscribeOn(Schedulers.io());

        Observable<String> observable2 = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                Log.d(TAG, "emitter s 1");
                e.onNext("a");
                Thread.sleep(1000);

                Log.d(TAG, "emitter s 2");
                e.onNext("b");
                Thread.sleep(1000);

                Log.d(TAG, "emitter s 3");
                e.onNext("c");
                Thread.sleep(1000);

                Log.d(TAG, "emitter s onComplete");
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io());

        Observable.zip(observable1, observable2, new BiFunction<Integer, String, String>() {
            @Override
            public String apply(@NonNull Integer integer, @NonNull String s) throws Exception {
                return integer + s;
            }
        })
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        Log.d(TAG, "onSubscribe");
                    }

                    @Override
                    public void onNext(@NonNull String s) {
                        Log.d(TAG, "onNext-->" + s);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d(TAG, "onError");
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete");
                    }
                });

        /*
        * log输出日志
        *com.rxjava.nan.rxjava_demo01 D/RxJava04Activity: onSubscribe
        *com.rxjava.nan.rxjava_demo01 D/RxJava04Activity: emit 1
        *com.rxjava.nan.rxjava_demo01 D/RxJava04Activity: emit A
        *com.rxjava.nan.rxjava_demo01 D/RxJava04Activity: onNext: 1A
        *com.rxjava.nan.rxjava_demo01 D/RxJava04Activity: emit B
        *com.rxjava.nan.rxjava_demo01 D/RxJava04Activity: emit 2
        *com.rxjava.nan.rxjava_demo01 D/RxJava04Activity: onNext: 2B
        *com.rxjava.nan.rxjava_demo01 D/RxJava04Activity: emit 3
        *com.rxjava.nan.rxjava_demo01 D/RxJava04Activity: emit C
        *com.rxjava.nan.rxjava_demo01 D/RxJava04Activity: emit complete2
        *com.rxjava.nan.rxjava_demo01 D/RxJava04Activity: onNext: 3C
        *com.rxjava.nan.rxjava_demo01 D/RxJava04Activity: onComplete
        *com.rxjava.nan.rxjava_demo01 D/RxJava04Activity: emit 4
        *com.rxjava.nan.rxjava_demo01 D/RxJava04Activity: emit complete1
        * */
        RxJavaPlugins.setErrorHandler(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                if (throwable instanceof InterruptedIOException) {
                    Log.d(TAG, "Io interrupted");
                }
            }
        });
    }

    /**
     * 实践
     * <p>
     * 比如一个界面需要展示用户的一些信息,
     * 而这些信息分别要从两个服务器接口中获取,
     * 而只有当两个都获取到了之后才能进行展示, 这个时候就可以用Zip了:
     */

    private void rxJava_test_04() {
        Retrofit retrofit = RetrofitNetWork.get();
        Api api = retrofit.create(Api.class);
        Map<String, String> map1 = new HashMap<>();
        map1.put("zip", "zip");

        Observable observable1 = api.getUserBaseInfo(map1).subscribeOn(Schedulers.io());
        Observable observable2 = api.getUserExtraInfo(map1).subscribeOn(Schedulers.io());

        Observable.zip(observable1, observable2,
                new BiFunction<UserBaseInfoResponse, UserExtraInfoResponse, UserInfo>() {
                    @Override
                    public UserInfo apply(@NonNull UserBaseInfoResponse userBaseInfoResponse,
                                          @NonNull UserExtraInfoResponse userExtraInfoResponse) throws Exception {
                        return new UserInfo(userBaseInfoResponse, userExtraInfoResponse);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<UserInfo>() {
                    @Override
                    public void accept(@NonNull UserInfo o) throws Exception {
                        //do something;
                    }
                });
    }

    /**
     * 给初学者的RxJava2.0教程(五)
     */
    private void intentRxJava05() {
        Intent intent = new Intent(RxJava04Activity.this, RxJava05Activity.class);
        startActivity(intent);
    }
}
