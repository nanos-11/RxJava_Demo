package com.rxjava.nan.rxjava_demo01.rxjava;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.rxjava.nan.rxjava_demo01.R;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.concurrent.TimeUnit;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;

public class RxJava06Activity extends AppCompatActivity implements View.OnClickListener {


    private static Subscription mSubscription;
    private final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx_java06);
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
        findViewById(R.id.rxJava_drop_end).setOnClickListener(this);
        findViewById(R.id.rxJava_latest_end).setOnClickListener(this);
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
            case R.id.rxJava_drop_end:
                request(128);
                break;
            case R.id.rxJava06:
                rxJava_test_06();
                break;
            case R.id.rxJava_latest_end:
                request(128);
                break;
            case R.id.rxJava07:
                rxJava_test_07();
                break;
            case R.id.rxJava08:
                intentRxJava07();
                break;
        }
    }


    /**
     * Flowable 操作符
     */
    private void rxJava_test_01() {
        Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<Integer> e) throws Exception {
                Log.d(TAG, "emit 1");
                e.onNext(1);
                Log.d(TAG, "emit 2");
                e.onNext(2);
                Log.d(TAG, "emit 3");
                e.onNext(3);
                Log.d(TAG, "emit complete");
                e.onComplete();
            }
        }, BackpressureStrategy.ERROR)//增加了一个参数创建Flowable的时候增加了一个参数,
                // 这个参数是用来选择背压,也就是出现上下游流速不均衡的时候应该怎么处理的办法
                //.subscribeOn(Schedulers.io())//让上下游工作在不同的线程
                //.observeOn(AndroidSchedulers.mainThread())//让上下游工作在不同的线程
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        Log.d(TAG, "onSubscribe");
                        //另外的一个区别是在下游的onSubscribe方法中传给我们的不再是Disposable了,
                        // 而是Subscription, 它俩有什么区别呢, 首先它们都是上下游中间的一个开关,
                        // 之前我们说调用Disposable.dispose()方法可以切断水管,
                        // 同样的调用Subscription.cancel()也可以切断水管,
                        // 不同的地方在于Subscription增加了一个void request(long n)方法,
                        // 这个方法有什么用呢, 在代码中也有这么一句代码:
                        s.request(1);  //注意这句代码
                        /*
                         如果去掉s.request(Long.MAX_VALUE);Log的输出日志
                         onSubscribe
                         emit 1
                         onError
                         emit 2
                         emit 3
                         emit complete   */
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.d(TAG, "onNext");
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
            运行结果
         D/TAG: onSubscribe
         D/TAG: emit 1
         D/TAG: onNext: 1
         D/TAG: emit 2
         D/TAG: onNext: 2
         D/TAG: emit 3
         D/TAG: onNext: 3
         D/TAG: emit complete
         D/TAG: onComplete
         *
         */
    }

    /**
     * 这是因为Flowable在设计的时候采用了一种新的思路也就是响应式拉取的方式来更好的解决上下游流速不均衡的问题,
     * 与我们之前所讲的控制数量和控制速度不太一样, 这种方式用通俗易懂的话来说就好比是叶问打鬼子, 我们把上游看成小日本,
     * 把下游当作叶问, 当调用Subscription.request(1)时, 叶问就说我要打一个! 然后小日本就拿出一个鬼子给叶问, 让他打,
     * 等叶问打死这个鬼子之后, 再次调用request(10), 叶问就又说我要打十个! 然后小日本又派出十个鬼子给叶问,
     * 然后就在边上看热闹, 看叶问能不能打死十个鬼子, 等叶问打死十个鬼子后再继续要鬼子接着打...
     * <p>
     * 所以我们把request当做是一种能力, 当成下游处理事件的能力, 下游能处理几个就告诉上游我要几个,
     * 这样只要上游根据下游的处理能力来决定发送多少事件, 就不会造成一窝蜂的发出一堆事件来, 从而导致OOM.
     * 这也就完美的解决之前我们所学到的两种方式的缺陷, 过滤事件会导致事件丢失, 减速又可能导致性能损失.
     * 而这种方式既解决了事件丢失的问题, 又解决了速度的问题, 完美 !
     * <p>
     * 但是太完美的东西也就意味着陷阱也会很多, 你可能只是被它的外表所迷惑, 失去了理智, 如果你滥用或者不遵守规则,
     * 一样会吃到苦头.
     * <p>
     * 比如这里需要注意的是, 只有当上游正确的实现了如何根据下游的处理能力来发送事件的时候, 才能达到这种效果,
     * 如果上游根本不管下游的处理能力, 一股脑的瞎他妈发事件, 仍然会产生上下游流速不均衡的问题,
     * 这就好比小日本管他叶问要打几个, 老子直接拿出1万个鬼子, 这尼玛有种打死给我看看? 那么如何正确的去实现上游呢,
     * 这里先卖个关子, 之后我们再来讲解.
     * <p>
     * 学习了request, 我们就可以解释上面的两段代码了.
     * 首先第一个同步的代码, 为什么上游发送第一个事件后下游就抛出了MissingBackpressureException异常,
     * 这是因为下游没有调用request, 上游就认为下游没有处理事件的能力, 而这又是一个同步的订阅, 既然下游处理不了,
     * 那上游不可能一直等待吧, 如果是这样, 万一这两根水管工作在主线程里, 界面不就卡死了吗, 因此只能抛个异常来提醒我们.
     * 那如何解决这种情况呢, 很简单啦, 下游直接调用request(Long.MAX_VALUE)就行了,
     * 或者根据上游发送事件的数量来request就行了, 比如这里request(3)就可以了.
     * <p>
     * 然后我们再来看看第二段代码, 为什么上下游没有工作在同一个线程时, 上游却正确的发送了所有的事件呢?
     * 这是因为在Flowable里默认有一个大小为128的水缸, 当上下游工作在不同的线程中时, 上游就会先把事件发送到这个水缸中,
     * 因此, 下游虽然没有调用request, 但是上游在水缸中保存着这些事件, 只有当下游调用request时, 才从水缸里取出事件发给下游.
     * 是不是这样呢, 我们来验证一下:
     * Subscription保存起来, 在界面上增加了一个按钮, 点击一次就调用Subscription.request(1)
     */
    private void rxJava_test_02() {
        Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<Integer> e) throws Exception {
                Log.d(TAG, "emit 1");
                e.onNext(1);
                Log.d(TAG, "emit 2");
                e.onNext(2);
                Log.d(TAG, "emit 3");
                e.onNext(3);
                Log.d(TAG, "emit complete");
                e.onComplete();
            }
        }, BackpressureStrategy.ERROR)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<Integer>() {
            @Override
            public void onSubscribe(Subscription s) {
                Log.d(TAG, "onSubscribe");
                mSubscription = s;
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
    }


    /**
     * 调用Subscription.request(1)
     */
    private void rxJava_test_03() {
        request(1);
    }


    /**
     * 我们先来思考一下, 发送128个事件没有问题是因为FLowable内部有一个大小为128的水缸,
     * 超过128就会装满溢出来, 那既然你水缸这么小, 那我给你换一个大水缸如何
     */
    private void rxJava_test_04() {
        Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<Integer> e) throws Exception {
                for (int i = 0; i < 1000; i++) {
                    Log.d(TAG, "emit " + i);
                    e.onNext(i);
                }
                // 这时的FLowable表现出来的特性的确和Observable一模一样,
                // 因此, 如果你像这样单纯的使用FLowable, 同样需要注意OOM的问题
                // 例：
                //for (int i = 0; ; i++) {
                //   Log.d(TAG, "emit " + i);
                //   e.onNext(i);
                //}
            }
        }, BackpressureStrategy.BUFFER).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<Integer>() {
            @Override
            public void onSubscribe(Subscription s) {
                Log.d(TAG, "onSubscribe");
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
          D/TAG: onSubscribe
          D/TAG: emit 0
          D/TAG: emit 1
          D/TAG: emit 2
          ...
          D/TAG: emit 997
          D/TAG: emit 998
          D/TAG: emit 999
         */
    }

    /**
     * Drop就是直接把存不下的事件丢弃,Latest就是只保留最新的事件, 来看看它们的实际效果吧.
     */
    private void rxJava_test_05() {
        Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<Integer> e) throws Exception {
                for (int i = 0; ; i++) {
                    e.onNext(i);
                }
            }
        }, BackpressureStrategy.DROP).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<Integer>() {
            @Override
            public void onSubscribe(Subscription s) {
                Log.d(TAG, "onSubscribe");
                mSubscription = s;
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
    }

    /**
     * Drop就是直接把存不下的事件丢弃,Latest就是只保留最新的事件, 来看看它们的实际效果吧.
     */
    private void rxJava_test_06() {
        Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<Integer> e) throws Exception {
                for (int i = 0; ; i++) {
                    e.onNext(i);
                }
            }
        }, BackpressureStrategy.LATEST).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<Integer>() {
            @Override
            public void onSubscribe(Subscription s) {
                Log.d(TAG, "onSubscribe");
                mSubscription = s;
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
         *  看上去好像和Drop差不多啊, Latest也首先保存了0-127这128个事件,
         *  等下游把这128个事件处理了之后才进行之后的处理, 光从这里没有看出有任何区别啊...
         */
    }


    /**
     * RxJava中的interval操作符,
     * <p>
     * 一运行就抛出了MissingBackpressureException异常, 提醒我们发太多了,
     * 那么怎么办呢, 这个又不是我们自己创建的FLowable啊...
     * <p>
     * 别慌, 虽然不是我们自己创建的, 但是RxJava给我们提供了其他的方法:
     * <p>
     * onBackpressureBuffer()
     * onBackpressureDrop()
     * onBackpressureLatest()
     */
    private void rxJava_test_07() {
        Flowable.interval(1, TimeUnit.MICROSECONDS)
                .onBackpressureDrop()
//                .onBackpressureLatest()
//                .onBackpressureBuffer()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Long>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        Log.d(TAG, "onSubscribe");
                        mSubscription = s;
                        s.request(Long.MAX_VALUE);
                    }

                    @Override
                    public void onNext(Long aLong) {
                        Log.d(TAG, "onNext:" + aLong);
                        try {
                            Thread.sleep(1000);  //延时1秒
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
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
    }


    private void intentRxJava07() {
        Intent intent = new Intent(this, RxJava07Activity.class);
        startActivity(intent);
    }

    private static void request(long n) {
        Log.d("RxJava06Activity", "n：" + n);
        mSubscription.request(n);
    }
}
