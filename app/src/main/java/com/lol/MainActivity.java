package com.lol;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.lol.boring.Event;
import com.lol.boring.Timeline;
import com.lol.boring.UserInfo;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import rx.subscriptions.Subscriptions;
import rx.util.functions.Func1;
import rx.util.functions.Func2;

public class MainActivity extends Activity implements Observer<Timeline> {

    private Adapter adapter;
    private Observable<Timeline> timeline;
    private Subscription subscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView eventList = (ListView) findViewById(R.id.timeline);
        adapter = new Adapter(this);
        eventList.setAdapter(adapter);

        // create a recurring ticker
        Observable<Long> ticker = Observable.timer(0, 15, TimeUnit.SECONDS);

        // map each of those ticks to an api request
        Observable<List<Event>> events = ticker.flatMap(new Func1<Long, Observable<List<Event>>>() {
            @Override
            public Observable<List<Event>> call(Long requestId) {
                Log.i("meow", "fetching events");
                Api api = new Api();
                return api.events();
            }
        });

        // load the user info from disk
        Observable<UserInfo> userInfo = new Api().userInfo(this);

        // i'm not sure this work 100% like i expect, but when the user info and the network calls complete, combine the two
        Observable<List<Event>> eventsWithReadInfo = events.zip(userInfo, new Func2<List<Event>, UserInfo, List<Event>>() {
            @Override
            public List<Event> call(List<Event> events, UserInfo user) {
                for (Event e : events) {
                    e.read = user.hasRead(e.id);
                }
                return events;
            }
        });

        // when a list of events comes in, partition them into past and future and emit a timeline
        Observable<Timeline> timeline = events.flatMap(new Func1<List<Event>, Observable<Timeline>>() {
            @Override
            public Observable<Timeline> call(final List<Event> events) {
                return Observable.create(new Observable.OnSubscribeFunc<Timeline>() {
                    @Override
                    public Subscription onSubscribe(Observer<? super Timeline> observer) {
                        Log.i("meow", "partitioning events");
                        final List<Event> newEvents = new ArrayList<Event>();
                        final List<Event> pastEvents = new ArrayList<Event>();
                        final DateTime fakeLastSeenTime = DateTime.now();

                        for (Event e : events) {
                            if (e.occursAt.isAfter(fakeLastSeenTime)) {
                                newEvents.add(e);
                            } else {
                                pastEvents.add(e);
                            }
                        }
                        observer.onNext(new Timeline(pastEvents, newEvents));
//                        observer.onCompleted();
                        return Subscriptions.empty();
                    }
                });
            }
        });

        // create an observable timeline that can deal with an activity's lifecycle
        this.timeline = AndroidObservable.fromActivity(this, timeline);
    }

    @Override protected void onResume() {
        super.onResume();
        Log.i("meow", "subscribing to event stream");
        subscription = timeline.subscribe(this);
    }

    @Override protected void onPause() {
        subscription.unsubscribe();
        Log.i("meow", "unsubscribed from event stream");
        super.onPause();
    }

    @Override
    public void onNext(Timeline timeline) {
        Log.i("meow", "updating ui");
        Log.i("meow", String.format("first event: %s", timeline.pastEvents.get(0)));
        adapter.clear();
        adapter.addAll(timeline.pastEvents);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onCompleted() {
        // won't ever happen.
        Log.i("meow", "done.");
    }

    @Override
    public void onError(Throwable throwable) {
        Log.e("meow", Log.getStackTraceString(throwable));
    }

    private static class Adapter extends ArrayAdapter<Event> {

        public Adapter(Context context) {
            super(context, android.R.layout.simple_list_item_1, android.R.id.text1, new ArrayList<Event>(0));
        }
    }

}
