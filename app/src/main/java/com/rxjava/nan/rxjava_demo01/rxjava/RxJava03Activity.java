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
import com.rxjava.nan.rxjava_demo01.response.RegisterResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class RxJava03Activity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx_java03);

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
                intentRxJava04();
                break;
        }
    }

    /**
     * map是RxJava中最简单的一个变换操作符了,
     * 它的作用就是对上游发送的每一个事件应用一个函数,
     * 使得每一个事件都按照指定的函数去变化
     */
    private void rxJava_test_01() {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                e.onNext("1");
                e.onNext("2");
                e.onNext("3");
            }
        }).map(new Function<String, Integer>() {
            @Override
            public Integer apply(@NonNull String s) throws Exception {
                return Integer.parseInt(s);
            }
        }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(@NonNull Integer integer) throws Exception {
                Log.i(TAG, "INTEGER-->" + integer);
            }
        });
    }

    /**
     * FlatMap将一个发送事件的上游Observable变换为多个发送事件的Observables，
     * 然后将它们发射的事件合并后放进一个单独的Observable里.
     */
    private void rxJava_test_02() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
            }
        }).flatMap(new Function<Integer, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(@NonNull Integer integer) throws Exception {
                List<String> stringList = new ArrayList<>();
                for (int i = 0; i < 3; i++) {
                    stringList.add("I am value " + integer);
                }
                return Observable.fromIterable(stringList).delay(10, TimeUnit.MILLISECONDS);
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(@NonNull String s) throws Exception {
                Log.d(TAG, "S-->" + s);
            }
        });
    }

    /**
     * 它和flatMap的作用几乎一模一样, 只是它的结果是严格按照上游发送的顺序来发送的
     */
    private void rxJava_test_03() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
            }
        }).concatMap(new Function<Integer, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(@NonNull Integer integer) throws Exception {
                List<String> stringList = new ArrayList<>();
                for (int i = 0; i < 3; i++) {
                    stringList.add("I am value " + integer);
                }
                return Observable.fromIterable(stringList).delay(10, TimeUnit.MILLISECONDS);
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(@NonNull String s) throws Exception {
                Log.d(TAG, "S-->" + s);
            }
        });
    }

    /**
     * 模拟 注册成功后登陆
     */
    private void rxJava_test_04() {
        Retrofit retrofit = RetrofitNetWork.get();
        final Api api = retrofit.create(Api.class);
        Map<String, String> mapRegister = new HashMap<>();
        mapRegister.put("register", "1");
        api.register(mapRegister)     //发起注册流程
                .subscribeOn(Schedulers.io())   //在io线程中进行网络操作
                .observeOn(AndroidSchedulers.mainThread())   //回到主线程处理注册结果
                .doOnNext(new Consumer<RegisterResponse>() {
                    @Override
                    public void accept(@NonNull RegisterResponse registerResponse) throws Exception {
                        //先根据注册的响应结果去做一操作
                        Log.d(TAG, "注册成功");
                    }
                })
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        Log.d(TAG, "失败");
                    }
                })
                .observeOn(Schedulers.io())  //在io线程中进行登录操作
                .flatMap(new Function<RegisterResponse, Observable<LoginResponse>>() {
                    @Override
                    public Observable<LoginResponse> apply(@NonNull RegisterResponse registerResponse) throws Exception {
                        Map<String, String> map = new HashMap<>();
                        map.put("login", "2");
                        return api.login(map);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<LoginResponse>() {
                    @Override
                    public void accept(@NonNull LoginResponse loginResponse) throws Exception {
                        Toast.makeText(RxJava03Activity.this, "登录成功", Toast.LENGTH_SHORT).show();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        Toast.makeText(RxJava03Activity.this, "登录失败", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    /**
     * 给初学者的RxJava2.0教程(四)
     */
    private void intentRxJava04() {
        Intent intent = new Intent(RxJava03Activity.this, RxJava04Activity.class);
        startActivity(intent);
    }
}
