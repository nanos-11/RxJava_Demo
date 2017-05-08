package com.rxjava.nan.rxjava_demo01.rxjava;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.rxjava.nan.rxjava_demo01.R;
import com.rxjava.nan.rxjava_demo01.network.Api;
import com.rxjava.nan.rxjava_demo01.network.RetrofitNetWork;
import com.rxjava.nan.rxjava_demo01.response.LoginResponse;

import java.util.HashMap;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 给初学者的RxJava2.0教程(二)
 * <p>
 * RxJava强大的线程控制.
 * <p>
 * 在RxJava中, 已经内置了很多线程选项供我们选择, 例如有
 * Schedulers.io() 代表io操作的线程, 通常用于网络,读写文件等io密集型的操作
 * Schedulers.computation() 代表CPU计算密集型的操作, 例如需要大量计算的操作
 * Schedulers.newThread() 代表一个常规的新线程
 * AndroidSchedulers.mainThread() 代表Android的主线程
 *
 * @author nan
 * @see <a href="http://www.jianshu.com/u/c50b715ccaeb">给初学者的RxJava2.0教程(二)</a>
 */
public class RxJava02Activity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx_java02);

        //Log.d(TAG, Thread.currentThread().getName()); //得到的当前线程为D/TAG: main

        initViewListener();
    }

    private void initViewListener() {
        findViewById(R.id.rxJava01).setOnClickListener(this);
        findViewById(R.id.rxJava02).setOnClickListener(this);
        findViewById(R.id.rxJava03).setOnClickListener(this);
        findViewById(R.id.rxJava04).setOnClickListener(this);
        findViewById(R.id.rxJava05).setOnClickListener(this);
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
            case R.id.rxJava05:
                rxJava_test_05();
                break;
            case R.id.rxJava08:
                intentRxJava03();
                break;
        }
    }

    /**
     * 当我们在主线程中去创建一个上游Observable来发送事件, 则这个上游默认就在主线程发送事件.
     * 当我们在主线程去创建一个下游Observer来接收事件, 则这个下游默认就在主线程中接收事件
     */
    private void rxJava_test_01() {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                Log.d(TAG, Thread.currentThread().getName());
                Log.d(TAG, "emit");
                e.onNext("1");
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(@NonNull String s) throws Exception {
                Log.d(TAG, Thread.currentThread().getName());
                Log.d(TAG, "onNext：" + s);
            }
        });
    }

    /**
     * 子线程中发送事件, 然后再改变下游的线程, 让它去主线程接收事件
     */
    private void rxJava_test_02() {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                Log.d(TAG, Thread.currentThread().getName());
                Log.d(TAG, "emit");
                e.onNext("1");
            }
        })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        Log.d(TAG, Thread.currentThread().getName());
                        Log.d(TAG, "onNext：" + s);
                    }
                });
    }


    /**
     * 多次指定上游的线程只有第一次指定的有效,多次指定下游的线程是可以的
     */
    private void rxJava_test_03() {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                Log.d(TAG, Thread.currentThread().getName());
                Log.d(TAG, "emit");
                e.onNext("1");
            }
        })
                .subscribeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        Log.d(TAG, Thread.currentThread().getName());
                        Log.d(TAG, "onNext：" + s);
                    }
                });
    }

    /**
     * 为了更清晰的看到下游的线程切换过程, 我们加点log:
     */
    private void rxJava_test_04() {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                Log.d(TAG, Thread.currentThread().getName());
                Log.d(TAG, "emit");
                e.onNext("1");
            }
        })
                .subscribeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        Log.d(TAG, "After observeOn(mainThread), current thread is :" + Thread.currentThread().getName());
                        Log.d(TAG, "onNext：" + s);
                    }
                })
                .observeOn(Schedulers.io())
                .doOnNext(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        Log.d(TAG, "After observeOn(io), current thread is : " + Thread.currentThread().getName());
                    }
                })
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        Log.d(TAG, Thread.currentThread().getName());
                        Log.d(TAG, "onNext：" + s);
                    }
                });
    }


    /**
     * 网络请求 模拟登陆
     */
    //切断所有的水管
    //CompositeDisposable c = new CompositeDisposable();
    private void rxJava_test_05() {
        RetrofitNetWork.get().create(Api.class).login(new HashMap<String, String>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<LoginResponse>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        //c.add(d);  //添加水管，退出Activity的时候, 调用CompositeDisposable.clear() 即可切断所有的水管.
                    }

                    @Override
                    public void onNext(@NonNull LoginResponse loginResponse) {
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Toast.makeText(RxJava02Activity.this, "登录失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {
                        Toast.makeText(RxJava02Activity.this, "登录成功", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    /**
     * 给初学者的RxJava2.0教程(三)
     */
    private void intentRxJava03() {
        Intent intent = new Intent(RxJava02Activity.this, RxJava03Activity.class);
        startActivity(intent);
    }
}
