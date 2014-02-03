package com.lol;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.lol.boring.Event;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.util.functions.Func1;
import rx.util.functions.Func2;

import static rx.android.observables.AndroidObservable.fromActivity;

public class MainActivity extends Activity implements Observer<Set<Event>> {

    private Adapter adapter;
    private Observable<Set<Event>> observableTimeline;
    private Subscription subscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView eventList = (ListView) findViewById(R.id.timeline);
        adapter = new Adapter(this);
        eventList.setAdapter(adapter);

        // create an observable set of current events every 15s
        Observable<Set<Event>> events = Observable.timer(0, 15, TimeUnit.SECONDS)
                // map ticks into api calls
                .flatMap(new Func1<Long, Observable<Event>>() {
                    @Override public Observable<Event> call(Long requestId) {
                        Log.i("meow", "fetching events");
                        Api api = new Api();
                        return api.events();
                    }})
                // filter out future events
                .filter(new Func1<Event, Boolean>() {
                    @Override public Boolean call(Event event) {
                        return event.occursAt.isBeforeNow();
                    }})
                // cram events into a sorted set
                .scan(new TreeSet<Event>(), new Func2<Set<Event>, Event, Set<Event>>() {
                    @Override public Set<Event> call(Set<Event> events, Event event) {
                        events.add(event);
                        return events;
                    }})
                // don't flood the UI with updates
                .debounce(1, TimeUnit.SECONDS);

        // activity lifecycle aware observable
        observableTimeline = fromActivity(this, events);
    }

    @Override protected void onResume() {
        super.onResume();
        Log.i("meow", "subscribing to event stream");
        subscription = observableTimeline.subscribe(this);
    }

    @Override protected void onPause() {
        subscription.unsubscribe();
        Log.i("meow", "unsubscribed from event stream");
        super.onPause();
    }

    @Override
    public void onNext(Set<Event> events) {
        Log.i("meow", "updating ui" + events.size());
        adapter.clear();
        adapter.addAll(events);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onCompleted() {
        // shouldn't ever happen.
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
