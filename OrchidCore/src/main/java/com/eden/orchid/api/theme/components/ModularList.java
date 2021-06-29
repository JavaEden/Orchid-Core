package com.eden.orchid.api.theme.components;

import clog.Clog;
import com.eden.common.util.EdenUtils;
import com.eden.orchid.api.OrchidContext;
import kotlin.collections.CollectionsKt;

import javax.inject.Provider;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public abstract class ModularList<L extends ModularList<L, I>, I extends ModularListItem<L, I>> {
    private final Function<OrchidContext, Map<String, Class<I>>> itemTypesProvider;
    private Map<String, Class<I>> itemTypes;
    protected List<Map<String, Object>> itemsJson;
    protected List<I> loadedItems;
    private boolean initialized = false;
    private String typeKey = "type";
    private String defaultType = null;

    @Inject
    public ModularList() {
        this.itemTypesProvider = (OrchidContext context) -> {
            Set<I> itemTypes = context.resolveSet(getItemClass());
            HashMap<String, Class<I>> itemTypesMap = new HashMap<>();
            for (I itemType : itemTypes) {
                if(isTypeEligible(itemType)) {
                    itemTypesMap.put(itemType.getType(), (Class<I>) itemType.getClass());
                }
            }
            return itemTypesMap;
        };
    }

    public ModularList(Function<OrchidContext, Map<String, Class<I>>> itemTypesProvider) {
        this.itemTypesProvider = itemTypesProvider;
    }

    protected boolean isTypeEligible(I item) {
        return true;
    }

    protected abstract Class<I> getItemClass();

    public void initialize(OrchidContext context, List<Map<String, Object>> itemsJson) {
        if (!initialized) {
            this.itemTypes = itemTypesProvider.apply(context);
            this.itemsJson = itemsJson;
            initialized = true;
        }
    }

    public boolean isEmpty() {
        if (loadedItems != null) {
            if (loadedItems.size() > 0) {
                return false;
            }
        } else if (itemsJson != null) {
            if (itemsJson.size() > 0) {
                return false;
            }
        }
        return true;
    }

    public List<I> get(OrchidContext context) {
        if (loadedItems == null) {
            loadedItems = new ArrayList<>();
            for (int i = 0; i < itemsJson.size(); i++) {
                Map<String, Object> itemJson = itemsJson.get(i);
                String itemType = itemJson.getOrDefault(typeKey, "").toString();
                if (EdenUtils.isEmpty(itemType) && !EdenUtils.isEmpty(defaultType)) {
                    itemType = defaultType;
                }
                if (!EdenUtils.isEmpty(itemType)) {
                    if (itemTypes.containsKey(itemType)) {
                        I item = context.resolve(itemTypes.get(itemType));
                        item.setOrder((i + 1) * 10);
                        item.extractOptions(context, itemJson);
                        addItem(item, itemJson);
                    } else {
                        Clog.w("{} type [{}] could not be found in {} {}", getItemClass().getSimpleName(), itemType, ModularList.this.getClass().getSimpleName(), getLogMessage());
                    }
                } else {
                    Clog.w("{} type not given in {}{}", getItemClass().getSimpleName(), ModularList.this.getClass().getSimpleName(), getLogMessage());
                }
            }
            loadedItems.sort(Comparator.comparingInt(ModularListItem::getOrder));
        }
        return loadedItems;
    }

    protected void addItem(I item, Map<String, Object> itemJson) {
        loadedItems.add(item);
    }

    protected String getItemType() {
        return "";
    }

    protected String getLogMessage() {
        return "";
    }

    public void invalidate() {
        loadedItems = null;
    }

    public void add(Map<String, Object> menuItemJson) {
        invalidate();
        itemsJson.add(menuItemJson);
    }

    public void add(List<Map<String, Object>> menuItemsJson) {
        invalidate();
        itemsJson.addAll(menuItemsJson);
    }

    public void set(List<Map<String, Object>> menuItemsJson) {
        invalidate();
        itemsJson = menuItemsJson;
    }

    public List<Map<String, Object>> getItemsJson() {
        return this.itemsJson;
    }

    public String getTypeKey() {
        return this.typeKey;
    }

    public void setTypeKey(final String typeKey) {
        this.typeKey = typeKey;
    }

    public String getDefaultType() {
        return this.defaultType;
    }

    public void setDefaultType(final String defaultType) {
        this.defaultType = defaultType;
    }

    public Set<String> getValidTypes() {
        return itemTypes.keySet();
    }
}
