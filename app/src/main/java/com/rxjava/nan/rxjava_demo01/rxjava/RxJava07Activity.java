package com.rxjava.nan.rxjava_demo01.rxjava;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.rxjava.nan.rxjava_demo01.R;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;

public class RxJava07Activity extends AppCompatActivity implements View.OnClickListener {

    private static Subscription mSubscription;
    private final String TAG = this.getClass().getSimpleName();
    private TextView tv_file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx_java07);
        initViewListener();
    }

    private void initViewListener() {
        tv_file = (TextView) findViewById(R.id.tv_file);
        findViewById(R.id.rxJava01).setOnClickListener(this);
        findViewById(R.id.rxJava02).setOnClickListener(this);
        findViewById(R.id.rxJava03).setOnClickListener(this);
        findViewById(R.id.rxJava05).setOnClickListener(this);
        findViewById(R.id.rxJava06).setOnClickListener(this);
        findViewById(R.id.rxJava_drop_end).setOnClickListener(this);
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
            case R.id.rxJava05:
                rxJava_test_05();
                break;
            case R.id.rxJava06:
                rxJava_test_06();
                break;
            case R.id.rxJava_drop_end:
                request(96);
                break;
        }
    }

    /**
     * long requested();
     * 方法注释的意思就是当前外部请求的数量
     */
    private void rxJava_test_01() {
        Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<Integer> e) throws Exception {

                Log.d(TAG, "long requested();---" + e.requested());

                //Log.d(TAG, "before emit, requested = " + e.requested());
                //Log.d(TAG, "emit 1");
                //e.onNext(1);
                //Log.d(TAG, "after emit 1, requested = " + e.requested());
                //Log.d(TAG, "emit 2");
                //e.onNext(2);
                //Log.d(TAG, "after emit 2, requested = " + e.requested());
                //Log.d(TAG, "emit 3");
                //e.onNext(3);
                //Log.d(TAG, "after emit 3, requested = " + e.requested());
                //Log.d(TAG, "emit complete");
                //e.onComplete();
                //Log.d(TAG, "after emit complete, requested = " + e.requested());

                //同步线程requested 根据下游的request而定
                //异步线程requested的数量为128
            }
        }, BackpressureStrategy.ERROR)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        Log.d(TAG, "onSubscribe");
                        mSubscription = s;
                        request(1);
                        //request(100);//看来多次调用也没问题，做了加法。110
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.d(TAG, "onNext:" + integer);
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.d(TAG, "onError");
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete");
                    }
                });
        /*
        * 下游调用request(n) 告诉上游它的处理能力，上游每发送一个next事件之后，requested就减一，
        * 注意是next事件，complete和error事件不会消耗requested，当减到0时，则代表下游没有处理能力了，
        * 这个时候你如果继续发送事件，会发生什么后果呢？当然是MissingBackpressureException啦
        * */
    }

    /**
     * 区别于 rxJava_test_01的是选择背压的方式
     */
    private void rxJava_test_02() {
        Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<Integer> e) throws Exception {

                Log.d(TAG, "long requested();---" + e.requested());

                //Log.d(TAG, "before emit, requested = " + e.requested());
                //Log.d(TAG, "emit 1");
                //e.onNext(1);
                //Log.d(TAG, "after emit 1, requested = " + e.requested());
                //Log.d(TAG, "emit 2");
                //e.onNext(2);
                //Log.d(TAG, "after emit 2, requested = " + e.requested());
                //Log.d(TAG, "emit 3");
                //e.onNext(3);
                //Log.d(TAG, "after emit 3, requested = " + e.requested());
                //Log.d(TAG, "emit complete");
                //e.onComplete();
                //Log.d(TAG, "after emit complete, requested = " + e.requested());

                //同步线程requested 根据下游的request而定
                //异步线程requested的数量为128
            }
        }, BackpressureStrategy.DROP)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        Log.d(TAG, "onSubscribe");
                        mSubscription = s;
                        request(1);
                        //request(100);//看来多次调用也没问题，做了加法。110
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.d(TAG, "onNext:" + integer);
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.d(TAG, "onError");
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete");
                    }
                });
        /*
        * 下游调用request(n) 告诉上游它的处理能力，上游每发送一个next事件之后，requested就减一，
        * 注意是next事件，complete和error事件不会消耗requested，当减到0时，则代表下游没有处理能力了，
        * 这个时候你如果继续发送事件，会发生什么后果呢？当然是MissingBackpressureException啦
        * */
    }

    /**
     * 异步线程requested的数量为128
     */
    private void rxJava_test_03() {
        Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<Integer> e) throws Exception {

                Log.d(TAG, "long requested();---" + e.requested());

                //Log.d(TAG, "before emit, requested = " + e.requested());
                //Log.d(TAG, "emit 1");
                //e.onNext(1);
                //Log.d(TAG, "after emit 1, requested = " + e.requested());
                //Log.d(TAG, "emit 2");
                //e.onNext(2);
                //Log.d(TAG, "after emit 2, requested = " + e.requested());
                //Log.d(TAG, "emit 3");
                //e.onNext(3);
                //Log.d(TAG, "after emit 3, requested = " + e.requested());
                //Log.d(TAG, "emit complete");
                //e.onComplete();
                //Log.d(TAG, "after emit complete, requested = " + e.requested());

                //同步线程requested 根据下游的request而定
                //异步线程requested的数量为128
            }
        }, BackpressureStrategy.ERROR)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        Log.d(TAG, "onSubscribe");
                        mSubscription = s;
                        request(1);
                        //request(100);//看来多次调用也没问题，做了加法。110
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.d(TAG, "onNext:" + integer);
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.d(TAG, "onError");
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete");
                    }
                });
        /*
        * 下游调用request(n) 告诉上游它的处理能力，上游每发送一个next事件之后，requested就减一，
        * 注意是next事件，complete和error事件不会消耗requested，当减到0时，则代表下游没有处理能力了，
        * 这个时候你如果继续发送事件，会发生什么后果呢？当然是MissingBackpressureException啦
        * */
    }


    private void rxJava_test_05() {
        Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(FlowableEmitter<Integer> emitter) throws Exception {
                Log.d(TAG, "First requested = " + emitter.requested());
                boolean flag;
                for (int i = 0; ; i++) {
                    flag = false;
                    while (emitter.requested() == 0) {
                        if (!flag) {
                            Log.d(TAG, "Oh no! I can't emit value!");
                            flag = true;
                        }
                    }
                    emitter.onNext(i);
                    Log.d(TAG, "emit " + i + " , requested = " + emitter.requested());
                }
            }
        }, BackpressureStrategy.ERROR)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Integer>() {

                    @Override
                    public void onSubscribe(Subscription s) {
                        Log.d(TAG, "onSubscribe");
                        mSubscription = s;
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.d(TAG, "onNext: " + integer);
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.w(TAG, "onError: ", t);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete");
                    }
                });
    }

    /**
     * 实践
     * <p>
     * 这个例子是读取一个文本文件，需要一行一行读取，然后处理并输出，如果文本文件很大的时候，
     * 比如几十M的时候，全部先读入内存肯定不是明智的做法，因此我们可以一边读取一边处理，实现的代码如下：
     */
    private void rxJava_test_06() {
        practice1();
        try {
            Thread.sleep(10000000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void practice1() {
        Flowable.create(new FlowableOnSubscribe<String>() {
            @Override
            public void subscribe(FlowableEmitter<String> emitter) throws Exception {
                try {
                    InputStream open = getResources().getAssets().open("test.txt");
                    InputStreamReader inputReader = new InputStreamReader(open);
                    BufferedReader bufReader = new BufferedReader(inputReader);
                    String str;
                    while ((str = bufReader.readLine()) != null && !emitter.isCancelled()) {
                        while (emitter.requested() == 0) {
                            if (emitter.isCancelled()) {
                                break;
                            }
                        }
                        emitter.onNext(str);
                    }
                    bufReader.close();
                    inputReader.close();
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        }, BackpressureStrategy.ERROR)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        Log.d(TAG, "onSubscribe：");
                        mSubscription = s;
                        request(1);
                    }

                    @Override
                    public void onNext(String string) {
                        Log.d(TAG, "onNext：" + string);
                        Log.d(TAG, Thread.currentThread() + "");
                        tv_file.setText("onNext" + string);
                        try {
                            Thread.sleep(1000);
                            request(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.d(TAG, "onError：");
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete：");
                    }
                });
    }


    private static void request(long n) {
        Log.d("RxJava07Activity", "n：" + n);
        mSubscription.request(n);
    }
}
