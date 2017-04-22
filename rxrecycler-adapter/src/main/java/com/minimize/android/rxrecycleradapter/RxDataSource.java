package com.minimize.android.rxrecycleradapter;

import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Scheduler;
import io.reactivex.annotations.Beta;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;


/**
 * Created by ahmedrizwan on 26/12/2015.
 */
public class RxDataSource<DataType> {

  private List<DataType> mDataSet;
  private RxAdapter mRxAdapter;
  private RxAdapterForTypes<DataType> mRxAdapterForTypes;

  public RxDataSource(List<DataType> dataSet) {
    this.mDataSet = dataSet;
  }

  /***
   * Call this when binding with a single Item-Type
   *
   * @param recyclerView RecyclerView instance
   * @param item_layout Layout id
   * @param <LayoutBinding> ViewDataBinding Type for the layout
   * @return Observable for binding viewHolder
   */
  public <LayoutBinding extends ViewDataBinding> Observable<SimpleViewHolder<DataType, LayoutBinding>> bindRecyclerView(
      @NonNull final RecyclerView recyclerView, @NonNull @LayoutRes final int item_layout) {
    mRxAdapterForTypes = null;
    mRxAdapter = new RxAdapter(item_layout, mDataSet);
    recyclerView.setAdapter(mRxAdapter);
    return mRxAdapter.asObservable();
  }

  /***
   * Call this if you need access to the Adapter!
   * Warning: might return null!
   *
   * @return RxAdapter Instance
   */
  @Nullable public RxAdapter getRxAdapter() {
    return mRxAdapter;
  }

  /***
   * Call this if you need access to the Adapter!
   * Warning: might return null!
   *
   * @return RxAdapter Instance
   */
  @Nullable public RxAdapterForTypes<DataType> getRxAdapterForTypes() {
    return mRxAdapterForTypes;
  }

  /***
   * Call this when you want to bind with multiple Item-Types
   *
   * @param recyclerView RecyclerView instance
   * @param viewHolderInfoList List of ViewHolderInfos
   * @param viewTypeCallback Callback that distinguishes different view Item-Types
   * @return Observable for binding viewHolder
   */
  public Observable<TypesViewHolder<DataType>> bindRecyclerView(
      @NonNull final RecyclerView recyclerView,
      @NonNull final List<ViewHolderInfo> viewHolderInfoList,
      @NonNull final OnGetItemViewType viewTypeCallback) {
    mRxAdapter = null;
    mRxAdapterForTypes = new RxAdapterForTypes<>(mDataSet, viewHolderInfoList, viewTypeCallback);
    recyclerView.setAdapter(mRxAdapterForTypes);
    return mRxAdapterForTypes.asObservable();
  }

  /***
   * For setting base dataSet
   */
  public RxDataSource<DataType> updateDataSet(List<DataType> dataSet) {
    mDataSet = dataSet;
    return this;
  }

  /***
   * For updating Adapter
   */
  public void updateAdapter() {
    if (mRxAdapter != null) {
      //update the update
      mRxAdapter.updateDataSet(mDataSet);
    } else if (mRxAdapterForTypes != null) {
      mRxAdapterForTypes.updateDataSet(mDataSet);
    }
  }

  public RxDataSource<DataType> map(Function<? super DataType, ? extends DataType> func) {
    mDataSet = Observable.fromIterable(mDataSet).map(func).toList().blockingGet();
    return this;
  }

  public RxDataSource<DataType> filter(Predicate<? super DataType> predicate) {
    mDataSet = Observable.fromIterable(mDataSet).filter(predicate).toList().blockingGet();
    return this;
  }

  public final RxDataSource<DataType> last() {
    mDataSet = Observable.fromIterable(mDataSet).lastElement().toObservable().toList().blockingGet();// last().toList().toBlocking().first();
    return this;
  }

  public final RxDataSource<DataType> last(Predicate<? super DataType> predicate) {
    mDataSet = Observable.fromIterable(mDataSet).filter(predicate).lastElement().toObservable().toList().blockingGet();
    return this;
  }

  public final RxDataSource<DataType> lastOrDefault(DataType defaultValue) {
    mDataSet = Observable.fromIterable(mDataSet)
        .takeLast(1)
        .single(defaultValue)
        .toObservable()
        .toList()
        .blockingGet();
    return this;
  }

  public final RxDataSource<DataType> lastOrDefault(DataType defaultValue,
      Predicate<? super DataType> predicate) {
    mDataSet = Observable.fromIterable(mDataSet)
        .filter(predicate)
        .takeLast(1)
        .single(defaultValue)
        .toObservable()
        .toList()
        .blockingGet();
    return this;
  }

  public final RxDataSource<DataType> limit(int count) {
    mDataSet = Observable.fromIterable(mDataSet).take(count).toList().blockingGet();
    return this;
  }

  public RxDataSource<DataType> empty() {
    mDataSet = Collections.emptyList();
    return this;
  }

  public final <R> RxDataSource<DataType> concatMap(
      Function<? super DataType, ? extends Observable<? extends DataType>> func) {
    mDataSet = (List<DataType>) Observable.fromIterable(mDataSet).concatMap(func).toList().blockingGet();
    return this;
  }

  public final RxDataSource<DataType> concatWith(Observable<? extends DataType> t1) {
    mDataSet = Observable.fromIterable(mDataSet).concatWith(t1).toList().blockingGet();
    return this;
  }

  public RxDataSource<DataType> distinct() {
    mDataSet = Observable.fromIterable(mDataSet).distinct().toList().blockingGet(); //.toObservable().blockingFirst()
    return this;
  }

  public RxDataSource<DataType> distinct(Function<? super DataType, ? extends Object> keySelector) {
    mDataSet = Observable.fromIterable(mDataSet).distinct(keySelector).toList().blockingGet();
    return this;
  }

  public final RxDataSource<DataType> elementAt(int index) {
    mDataSet = Observable.fromIterable(mDataSet).elementAt(index).toObservable().toList().blockingGet();
    return this;
  }

  public final RxDataSource<DataType> elementAtOrDefault(int index, DataType defaultValue) {
    mDataSet = Observable.fromIterable(mDataSet)
        .elementAt(index, defaultValue)
        .toObservable()
        .toList()
        .blockingGet();
    return this;
  }

  public final RxDataSource<DataType> first() {
    mDataSet = Observable.fromIterable(mDataSet).firstElement().toObservable().toList().blockingGet();
    return this;
  }

  public final RxDataSource<DataType> first(Predicate<? super DataType> predicate) {
    mDataSet = Observable.fromIterable(mDataSet).filter(predicate).toList().blockingGet();
    return this;
  }

  public final RxDataSource<DataType> firstOrDefault(DataType defaultValue) {
    mDataSet = Observable.fromIterable(mDataSet).first(defaultValue).toObservable().toList().blockingGet();
    return this;
  }

    public final RxDataSource<DataType> firstOrDefault(DataType defaultValue, Predicate<? super DataType> predicate) {
        mDataSet = Observable.fromIterable(mDataSet)
                .filter(predicate)
                .first(defaultValue)
                .toObservable()
                .toList()
                .blockingGet();
        return this;
    }

  public final RxDataSource<DataType> flatMap(
      Function<? super DataType, ? extends Observable<? extends DataType>> func) {
    mDataSet = (List<DataType>) Observable.fromIterable(mDataSet).flatMap(func).toList().blockingGet();
    return this;
  }

  @Beta
  public final RxDataSource<DataType> flatMap(
          Function<? super DataType, ? extends Observable<? extends DataType>> func, int maxConcurrent) {
    mDataSet = (List<DataType>) Observable.fromIterable(mDataSet).flatMap(func, maxConcurrent).toList().blockingGet();
    return this;
  }

  public final RxDataSource<DataType> flatMap(
          Function<? super DataType, ? extends Observable<? extends DataType>> onNext,
          Function<? super Throwable, ? extends Observable<? extends DataType>> onError,
          Callable<? extends Observable<? extends DataType>> onCompleted) {
    mDataSet = (List<DataType>) Observable.fromIterable(mDataSet)
        .flatMap(onNext, onError, onCompleted)
        .toList()
        .blockingGet();
    return this;
  }

  @Beta public final RxDataSource<DataType> flatMap(
          Function<? super DataType, ? extends Observable<? extends DataType>> onNext,
          Function<? super Throwable, ? extends Observable<? extends DataType>> onError,
          Callable<? extends Observable<? extends DataType>> onCompleted, int maxConcurrent) {
    mDataSet = (List<DataType>) Observable.fromIterable(mDataSet)
        .flatMap(onNext, (Function<Throwable, ? extends ObservableSource<?>>) onError, onCompleted, maxConcurrent)
        .toList()
        .blockingGet();
    return this;
  }

  public final <U, R> RxDataSource<DataType> flatMap(
      final Function<? super DataType, ? extends Observable<? extends U>> collectionSelector,
      final BiFunction<? super DataType, ? super U, ? extends DataType> resultSelector) {
    mDataSet = Observable.fromIterable(mDataSet)
        .flatMap(collectionSelector, resultSelector)
        .toList()
        .blockingGet();
    return this;
  }

  public final RxDataSource<DataType> flatMapIterable(
          Function<? super DataType, ? extends Iterable<? extends DataType>> collectionSelector) {
    mDataSet =
        Observable.fromIterable(mDataSet).flatMapIterable(collectionSelector).toList().blockingGet();
    return this;
  }

  public final RxDataSource<DataType> reduce(BiFunction<DataType, DataType, DataType> accumulator) {
    mDataSet = Observable.fromIterable(mDataSet).reduce(accumulator).toObservable().toList().blockingGet();
    return this;
  }

  public final RxDataSource<DataType> reduce(DataType initialValue,
                                             BiFunction<DataType, ? super DataType, DataType> accumulator) {
    mDataSet =
        Observable.fromIterable(mDataSet).reduce(initialValue, accumulator).toObservable().toList().blockingGet();
    return this;
  }

  public final RxDataSource<DataType> repeat(final long count) {
    List<DataType> dataSet = mDataSet;
    mDataSet = Observable.fromIterable(dataSet).repeat(count).toList().blockingGet();
    return this;
  }

  public final RxDataSource<DataType> repeat(final long count, Scheduler scheduler) {
    mDataSet = Observable.fromIterable(mDataSet).repeat(count).toList().blockingGet();
    return this;
  }

  public final RxDataSource<DataType> take(final int count) {
    mDataSet = Observable.fromIterable(mDataSet).take(count).toList().blockingGet();
    return this;
  }

  public final RxDataSource<DataType> takeFirst(Predicate<? super DataType> predicate) {
    mDataSet = Observable.fromIterable(mDataSet).filter(predicate).firstElement().toObservable().toList().blockingGet();
    return this;
  }

  public final RxDataSource<DataType> takeLast(final int count) {
    mDataSet = Observable.fromIterable(mDataSet).takeLast(count).toList().blockingGet();
    return this;
  }
}
