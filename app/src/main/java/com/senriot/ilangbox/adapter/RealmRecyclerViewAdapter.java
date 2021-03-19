package com.senriot.ilangbox.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import io.realm.OrderedRealmCollection;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.RealmList;
import io.realm.RealmModel;
import io.realm.RealmResults;

public abstract class RealmRecyclerViewAdapter<T extends RealmModel, S extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<S> {
    private final boolean hasAutoUpdates;
    private final boolean updateOnModification;
    private final OrderedRealmCollectionChangeListener listener;
    @Nullable
    private OrderedRealmCollection<T> adapterData;

    private OrderedRealmCollectionChangeListener createListener() {
        return (collection, changeSet) ->
        {

            notifyDataSetChanged();
//                if (changeSet.getState() == OrderedCollectionChangeSet.State.INITIAL) {
//                    notifyDataSetChanged();
//                    return;
//                }
//                // For deletions, the adapter has to be notified in reverse order.
//                OrderedCollectionChangeSet.Range[] deletions = changeSet.getDeletionRanges();
//                for (int i = deletions.length - 1; i >= 0; i--) {
//                    OrderedCollectionChangeSet.Range range = deletions[i];
//                    notifyItemRangeRemoved(range.startIndex + dataOffset(), range.length);
//                }
//
//                OrderedCollectionChangeSet.Range[] insertions = changeSet.getInsertionRanges();
//                for (OrderedCollectionChangeSet.Range range : insertions) {
//                    notifyItemRangeInserted(range.startIndex + dataOffset(), range.length);
//                }
//
//                if (!updateOnModification) {
//                    return;
//                }
//
//                OrderedCollectionChangeSet.Range[] modifications = changeSet.getChangeRanges();
//                for (OrderedCollectionChangeSet.Range range : modifications) {
//                    notifyItemRangeChanged(range.startIndex + dataOffset(), range.length);
//                }
        };
    }

    /**
     * Returns the number of header elements before the Realm collection elements. This is needed so
     * all indexes reported by the {@link OrderedRealmCollectionChangeListener} can be adjusted
     * correctly. Failing to override can cause the wrong elements to animate and lead to runtime
     * exceptions.
     *
     * @return The number of header elements in the RecyclerView before the collection elements. Default is {@code 0}.
     */
    public int dataOffset() {
        return 0;
    }

    /**
     * This is equivalent to {@code RealmRecyclerViewAdapter(data, autoUpdate, true)}.
     *
     * @param data       collection data to be used by this adapter.
     * @param autoUpdate when it is {@code false}, the adapter won't be automatically updated when collection data
     *                   changes.
     * @see #RealmRecyclerViewAdapter(OrderedRealmCollection, boolean, boolean)
     */
    public RealmRecyclerViewAdapter(@Nullable OrderedRealmCollection<T> data, boolean autoUpdate) {
        this(data, autoUpdate, true);
    }

    /**
     * @param data                 collection data to be used by this adapter.
     * @param autoUpdate           when it is {@code false}, the adapter won't be automatically updated when collection data
     *                             changes.
     * @param updateOnModification when it is {@code true}, this adapter will be updated when deletions, insertions or
     *                             modifications happen to the collection data. When it is {@code false}, only
     *                             deletions and insertions will trigger the updates. This param will be ignored if
     *                             {@code autoUpdate} is {@code false}.
     */
    public RealmRecyclerViewAdapter(@Nullable OrderedRealmCollection<T> data, boolean autoUpdate,
                                    boolean updateOnModification) {
        if (data != null && !data.isManaged())
            throw new IllegalStateException("Only use this adapter with managed RealmCollection, " +
                    "for un-managed lists you can just use the BaseRecyclerViewAdapter");
        this.adapterData = data;
        this.hasAutoUpdates = autoUpdate;
        this.listener = hasAutoUpdates ? createListener() : null;
        this.updateOnModification = updateOnModification;
    }

    @Override
    public void onAttachedToRecyclerView(final RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        if (hasAutoUpdates && isDataValid()) {
            //noinspection ConstantConditions
            addListener(adapterData);
        }
    }

    @Override
    public void onDetachedFromRecyclerView(final RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        if (hasAutoUpdates && isDataValid()) {
            //noinspection ConstantConditions
            removeListener(adapterData);
        }
    }

    @Override
    public int getItemCount() {
        //noinspection ConstantConditions
        return isDataValid() ? adapterData.size() : 0;
    }

    /**
     * Returns the item in the underlying data associated with the specified position.
     * <p>
     * This method will return {@code null} if the Realm instance has been closed or the index
     * is outside the range of valid adapter data (which e.g. can happen if {@link #getItemCount()}
     * is modified to account for header or footer views.
     * <p>
     * Also, this method does not take into account any header views. If these are present, modify
     * the {@code index} parameter accordingly first.
     *
     * @param index index of the item in the original collection backing this adapter.
     * @return the item at the specified position or {@code null} if the position does not exists or
     * the adapter data are no longer valid.
     */
    @SuppressWarnings("WeakerAccess")
    @Nullable
    public T getItem(int index) {
        if (index < 0) {
            throw new IllegalArgumentException("Only indexes >= 0 are allowed. Input was: " + index);
        }

        // To avoid exception, return null if there are some extra positions that the
        // child adapter is adding in getItemCount (e.g: to display footer view in recycler view)
        if (adapterData != null && index >= adapterData.size()) return null;
        //noinspection ConstantConditions
        return isDataValid() ? adapterData.get(index) : null;
    }

    /**
     * Returns data associated with this adapter.
     *
     * @return adapter data.
     */
    @Nullable
    public OrderedRealmCollection<T> getData() {
        return adapterData;
    }

    /**
     * Updates the data associated to the Adapter. Useful when the query has been changed.
     * If the query does not change you might consider using the automaticUpdate feature.
     *
     * @param data the new {@link OrderedRealmCollection} to display.
     */
    @SuppressWarnings("WeakerAccess")
    public void updateData(@Nullable OrderedRealmCollection<T> data) {
        if (hasAutoUpdates) {
            if (isDataValid()) {
                //noinspection ConstantConditions
                removeListener(adapterData);
            }
            if (data != null) {
                addListener(data);
            }
        }

        this.adapterData = data;
        notifyDataSetChanged();
    }

    private void addListener(@NonNull OrderedRealmCollection<T> data) {
        if (data instanceof RealmResults) {
            RealmResults<T> results = (RealmResults<T>) data;
            //noinspection unchecked
            results.addChangeListener(listener);
        } else if (data instanceof RealmList) {
            RealmList<T> list = (RealmList<T>) data;
            //noinspection unchecked
            list.addChangeListener(listener);
        } else {
            throw new IllegalArgumentException("RealmCollection not supported: " + data.getClass());
        }
    }

    private void removeListener(@NonNull OrderedRealmCollection<T> data) {
        if (data instanceof RealmResults) {
            RealmResults<T> results = (RealmResults<T>) data;
            //noinspection unchecked
            results.removeChangeListener(listener);
        } else if (data instanceof RealmList) {
            RealmList<T> list = (RealmList<T>) data;
            //noinspection unchecked
            list.removeChangeListener(listener);
        } else {
            throw new IllegalArgumentException("RealmCollection not supported: " + data.getClass());
        }
    }

    private boolean isDataValid() {
        return adapterData != null && adapterData.isValid();
    }
}
