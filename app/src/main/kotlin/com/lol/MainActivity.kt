package com.lol

import android.app.Activity
import android.os.Bundle
import com.lol.boring.Event
import rx.Observer
import android.widget.ListView
import android.widget.ArrayAdapter
import java.util.ArrayList
import rx.Observable
import java.util.concurrent.TimeUnit
import java.util.TreeSet
import android.util.Log
import rx.android.observables.AndroidObservable
import rx.Subscription

public open class MainActivity : Activity(), Observer<Set<Event>> {
    private var adapter: ArrayAdapter<Event>? = null
    private var observableTimeline: Observable<TreeSet<Event>>? = null
    private var subscription: Subscription? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super<Activity>.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        adapter = ArrayAdapter<Event>(this, android.R.layout.simple_list_item_1, android.R.id.text1, ArrayList<Event>());
        val listView = findViewById(R.id.timeline) as ListView
        listView.setAdapter(adapter)

        // create an observable set of current events every 15s
        val events = Observable.timer(0, 15, TimeUnit.SECONDS)
        ?.flatMap { Api().events() } // map each tick to an api call.
        ?.filter { it?.occursAt?.isBeforeNow() } // filter out future events
        ?.scan(TreeSet<Event>(), {(es: TreeSet<Event>?, e: Event?) -> if (e != null) es?.add(e as Event); es }) // do a bunch of kotlin bookkeeping and put the event in the set
        ?.debounce(1, TimeUnit.SECONDS) // don't flood the UI with udpates

        // activity lifecycle aware observable, will subscribe on a thread pool and observe on the ui thread
        observableTimeline = AndroidObservable.fromActivity(this, events)
    }

    override fun onResume() {
        super<Activity>.onResume()
        subscription = observableTimeline?.subscribe(this)
    }

    override fun onPause() {
        subscription?.unsubscribe()
        super<Activity>.onPause()
    }

    override fun onNext(events: Set<Event>?) {
        Log.i("meow", "updating ui")
        adapter?.clear()
        adapter?.addAll(events)
        adapter?.notifyDataSetChanged()
    }

    override fun onCompleted() {
        Log.i("meow", "complete.")
    }

    override fun onError(error: Throwable?) {
        Log.e("meow", Log.getStackTraceString(error))
    }

}