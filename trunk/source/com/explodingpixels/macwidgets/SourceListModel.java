package com.explodingpixels.macwidgets;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/**
 * The backing model to be used with a {@link SourceList}.
 */
public final class SourceListModel {

    private List<SourceListCategory> fCategories = new ArrayList<SourceListCategory>();

    private List<SourceListModelListener> fListeners = new ArrayList<SourceListModelListener>();

    // SourceListCategory methods /////////////////////////////////////////////////////////////////

    /**
     * Gets the {@link SourceListCategory}s associated with this model.
     * @return the {@link SourceListCategory}s associated with this model.
     */
    public List<SourceListCategory> getCategories() {
        return Collections.unmodifiableList(fCategories);
    }

    /**
     * Adds the given category to the model and fires an event such that
     * {@link SourceListModelListener}s will be notified.
     * @param category the {@link SourceListCategory} to add.
     */
    public void addCategory(SourceListCategory category) {
        fCategories.add(category);
        fireCategoryAdded(category);
    }

    /**
     * Adds the given category to the model at the given index and fires an event such that
     * {@link SourceListModelListener}s will be notified.
     * @param index the index to add the category at.
     * @param category the {@link SourceListCategory} to add.
     */
    public void addCategory(int index, SourceListCategory category) {
        fCategories.add(index, category);
        fireCategoryAdded(category);
    }

    /**
     * Removes the given category from the model and fires an event such that
     * {@link SourceListModelListener}s will be notified.
     * @param category the {@link SourceListCategory} to remove.
     * @throws IllegalArgumentException if the given category is not part of this model.
     */
    public void removeCategory(SourceListCategory category) {
        boolean removed = fCategories.remove(category);
        if (!removed) {
            throw new IllegalArgumentException("The given category does not exist in this model.");
        }
        fireCategoryRemoved(category);
    }

    /**
     * Removes the category at the given index from the model and fires an event such that
     * {@link SourceListModelListener}s will be notified.
     * @param index the index of the {@link SourceListCategory} to remove.
     * @throws IllegalArgumentException if there is no category at the given index.
     */
    public void removeCategoryAt(int index) {
        SourceListCategory category = fCategories.remove(index);
        if (category == null) {
            throw new IllegalArgumentException("There is no category at the index " + index + ".");
        }
        fireCategoryRemoved(category);
    }

    // SourceListItem methods /////////////////////////////////////////////////////////////////////

    /**
     * Adds the given item to the given {@link SourceListCategory}.
     * @param item the item to add.
     * @param category the category to add the item to.
     * @throws IllegalStateException if the given category is not in the model.
     */
    public void addItemToCategory(SourceListItem item, SourceListCategory category) {
        addItemToCategory(item, category, category.getItemCount());
    }

    /**
     * Adds the given item to the given {@link SourceListCategory} at the given index within that
     * category.
     * @param item the item to add.
     * @param category the category to add the item to.
     * @param index the index in the category to add the item.
     * @throws IllegalStateException if the given category is not in the model.
     */
    public void addItemToCategory(SourceListItem item, SourceListCategory category, int index) {
        checkCategoryIsInModel(category);
        category.addItem(index, item);
        fireItemAddedToCategory(item, category);
    }

    /**
     * Adds the given "child" item to the given "parent" item.
     * @param childItem the item to add to the given parent item.
     * @param parentItem the item to add the child item to.
     * @throws IllegalStateException if the given parent item is not in the model.
     */
    public void addItemToItem(SourceListItem childItem, SourceListItem parentItem) {
        addItemToItem(childItem, parentItem, parentItem.getChildItems().size());
    }

    /**
     * Adds the given "child" item to the given "parent" item at the given index.
     * @param childItem the item to add to the given parent item.
     * @param parentItem the item to add the child item to.
     * @param index the index of the parent item at which to add the child item.
     * @throws IllegalStateException if the given child or parent item is not in the model.
     */
    public void addItemToItem(SourceListItem childItem, SourceListItem parentItem, int index) {
        checkItemIsInModel(parentItem);
        parentItem.addItem(index, childItem);
        fireItemAddedToItem(childItem, parentItem);
    }

    /**
     * Removes the given item from the given category.
     * @param item the item to remove from the given category.
     * @param category the category form which to remove the given item.
     * @throws IllegalStateException if the given category is not in the model.
     */
    public void removeItemFromCategory(SourceListItem item, SourceListCategory category) {
        checkItemIsInModel(item);
        checkCategoryIsInModel(category);
        category.removeItem(item);
        fireItemRemovedFromCategory(item, category);
    }

    /**
     * Removes the item at the given index from the given category.
     * @param index the index of the item to remove.
     * @param category the category from which to remove the item.
     * @throws IllegalStateException if the given category is not in the model.
     */
    public void removeItemFromCategoryAtIndex(int index, SourceListCategory category) {
        removeItemFromCategory(category.getItem(index), category);
    }

    /**
     * Removes the given child item at from the given parent item.
     * @param childItem the item to remove.
     * @param parentItem the item from which to remove the given child item.
     * @throws IllegalStateException if the given child or parent item is not in the model.
     */
    public void removeItemFromItem(SourceListItem childItem, SourceListItem parentItem) {
        checkItemIsInModel(childItem);
        checkItemIsInModel(parentItem);
        parentItem.removeItem(childItem);
        fireItemRemovedFromItem(childItem, parentItem);
    }

    /**
     * Removes the given child item at from the given parent item.
     * @param index the index of the item to remove.
     * @param parentItem the item from which to remove the given child item.
     * @throws IllegalStateException if the given child or parent item is not in the model.
     */
    public void removeItemFromItem(int index, SourceListItem parentItem) {
        checkItemIsInModel(parentItem);
        SourceListItem itemRemoved = parentItem.removeItem(index);
        fireItemRemovedFromItem(itemRemoved, parentItem);
    }

    // Utility methods. ///////////////////////////////////////////////////////////////////////////

    private void checkCategoryIsInModel(SourceListCategory category) {
        if (!fCategories.contains(category)) {
            throw new IllegalArgumentException("The " + category.getText() + " category is not part of this model.");
        }
    }

    private void checkItemIsInModel(SourceListItem item) {
        boolean found = false;
        for (SourceListCategory category : fCategories) {
            found = category.containsItem(item);
            if (found) {
                break;
            }
        }
        if (!found) {
            throw new IllegalArgumentException("The given item is not part of this model.");
        }
    }

    // SourceListModelListener support. ///////////////////////////////////////////////////////////

    private void fireCategoryAdded(SourceListCategory category) {
        for (SourceListModelListener listener : fListeners) {
            listener.categoryAdded(category);
        }
    }

    private void fireCategoryRemoved(SourceListCategory category) {
        for (SourceListModelListener listener : fListeners) {
            listener.categoryRemoved(category);
        }
    }

    private void fireItemAddedToCategory(SourceListItem item,
                                         SourceListCategory category) {
        for (SourceListModelListener listener : fListeners) {
            listener.itemAddedToCategory(item, category);
        }
    }

    private void fireItemRemovedFromCategory(SourceListItem item,
                                             SourceListCategory category) {
        for (SourceListModelListener listener : fListeners) {
            listener.itemRemovedFromCategory(item, category);
        }
    }

    private void fireItemAddedToItem(SourceListItem childItem,
                                     SourceListItem parentItem) {
        for (SourceListModelListener listener : fListeners) {
            listener.itemAddedToItem(childItem, parentItem);
        }
    }

    private void fireItemRemovedFromItem(SourceListItem childItem,
                                         SourceListItem parentItem) {
        for (SourceListModelListener listener : fListeners) {
            listener.itemRemovedFromItem(childItem, parentItem);
        }
    }

    /**
     * Adds the given {@link SourceListModelListener} to the list of listeners.
     * @param listener the listener to add.
     */
    public void addSourceListModelListener(SourceListModelListener listener) {
        fListeners.add(listener);
    }

    /**
     * Removes the given {@link SourceListModelListener} from the list of listeners.
     * @param listener the listener to remove.
     */
    public void removeSourceListModelListener(SourceListModelListener listener) {
        fListeners.remove(listener);
    }

}