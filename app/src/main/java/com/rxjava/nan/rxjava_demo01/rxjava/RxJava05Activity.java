package com.rxjava.nan.rxjava_demo01.rxjava;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.rxjava.nan.rxjava_demo01.R;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * 给初学者的RxJava2.0教程(五)
 */
public class RxJava05Activity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx_java05);
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
                intentRxJava06();
                break;
        }
    }


    /**
     * 我们分别创建了两根水管, 第一根水管用机器指令的执行速度来无限循环发送事件, 第二根水管随便发送点什么,
     * 由于我们没有发送Complete事件, 因此第一根水管会一直发事件到它对应的水缸里去, 我们来看看运行结果是什么样.
     */
    private void rxJava_test_01() {
        Observable<Integer> observable1 = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                for (int i = 0; ; i++) {   //无限循环发事件
                    emitter.onNext(i);
                    //内存占用以斜率为1的直线迅速上涨, 几秒钟就300多M , 最终报出了OOM:
                }
            }
        }).subscribeOn(Schedulers.io());

        Observable<String> observable2 = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                emitter.onNext("A");
            }
        }).subscribeOn(Schedulers.io());

        Observable.zip(observable1, observable2, new BiFunction<Integer, String, String>() {
            @Override
            public String apply(Integer integer, String s) throws Exception {
                return integer + s;
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.d(TAG, s);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.w(TAG, throwable);
                    }
                });
    }

    /**
     * 测试zip  同步和异步  内存消耗
     */
    private void rxJava_test_02() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                for (int i = 0; ; i++) {   //无限循环发事件
                    emitter.onNext(i);
                }
            }
        })
                // 上下游在同一个线程中 内存会消耗掉
                // 而上下游不在同一个线程中 内存会爆掉
                //  rxJava_test_03() 注释解释为什么会有这种情况
                // .subscribeOn(Schedulers.io())
                // .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Thread.sleep(2000);
                        Log.d(TAG, "" + integer);
                    }
                });
    }


    /**
     * 涉及了同步和异步的知识了.
     * 当上下游工作在同一个线程中时, 这时候是一个同步的订阅关系,
     * 也就是说上游每发送一个事件必须等到下游接收处理完了以后才能接着发送下一个事件.
     * 当上下游工作在不同的线程中时, 这时候是一个异步的订阅关系, 这个时候上游发送数据不需要等待下游接收,
     * 为什么呢, 因为两个线程并不能直接进行通信, 因此上游发送的事件并不能直接到下游里去,
     * 这个时候就需要一个田螺姑娘来帮助它们俩, 这个田螺姑娘就是我们刚才说的水缸 ! 上游把事件发送到水缸里去,
     * 下游从水缸里取出事件来处理,
     * 因此, 当上游发事件的速度太快, 下游取事件的速度太慢, 水缸就会迅速装满, 然后溢出来, 最后就OOM了.
     */


    /**
     * 只放我们需要的事件
     * 添加过滤器
     */
    private void rxJava_test_03() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter e) throws Exception {
                for (int i = 0; ; i++) {
                    e.onNext(i);
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .filter(new Predicate<Integer>() {
                    @Override
                    public boolean test(@NonNull Integer integer) throws Exception {
                        //通过减少进入水缸的事件数量的确可以缓解上下游流速不均衡的问题, 但是力度还不够
                        return integer % 100 == 0;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(@NonNull Integer o) throws Exception {
                        Log.d(TAG, "" + o);
                    }
                });
    }

    /**
     * 这里用了一个sample操作符, 简单做个介绍, 这个操作符每隔指定的时间就从上游中取出一个事件发送给下游.
     * 这里我们让它每隔2秒取一个事件给下游, 来看看这次的运行结果吧:
     */
    private void rxJava_test_04() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> e) throws Exception {
                for (int i = 0; ; i++) {
                    e.onNext(i);
                }
            }
        })
                .subscribeOn(Schedulers.io())
                //这次我们可以看到, 虽然上游仍然一直在不停的发事件, 但是我们只是每隔一定时间取一个放进水缸里,
                // 并没有全部放进水缸里, 因此这次内存仅仅只占用了5M.
                .sample(2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(@NonNull Integer integer) throws Exception {
                        Log.d(TAG, "integer：" + integer);
                    }
                });
    }

    /**
     * 前面这两种方法归根到底其实就是减少放进水缸的事件的数量,
     * 是以数量取胜, 但是这个方法有个缺点, 就是丢失了大部分的事件.
     * <p>
     * 那么我们换一个角度来思考, 既然上游发送事件的速度太快,
     * 那我们就适当减慢发送事件的速度, 从速度上取胜, 听上去不错, 我们来试试:
     */
    private void rxJava_test_05() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> e) throws Exception {
                for (int i = 0; ; i++) {
                    e.onNext(i);
                    Thread.sleep(2000);
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(@NonNull Integer integer) throws Exception {
                        Log.d(TAG, "integer：" + integer);
                    }
                });
    }

    /*
    * 总结：
    * 本节中的治理的办法就两种:
    * 一是从数量上进行治理, 减少发送进水缸里的事件
    * 二是从速度上进行治理, 减缓事件发送进水缸的速度
    *
    *
    * */


    private void intentRxJava06() {
        Intent intent = new Intent(RxJava05Activity.this, RxJava06Activity.class);
        startActivity(intent);
    }

}
