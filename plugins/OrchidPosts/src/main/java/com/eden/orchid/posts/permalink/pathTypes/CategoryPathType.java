package com.eden.orchid.posts.permalink.pathTypes;

import com.eden.orchid.api.theme.pages.OrchidPage;
import com.eden.orchid.posts.pages.PostArchivePage;
import com.eden.orchid.posts.pages.PostPage;
import com.eden.orchid.posts.permalink.PermalinkPathType;

import javax.inject.Inject;

public class CategoryPathType extends PermalinkPathType {

    @Inject
    public CategoryPathType() {
        super(100);
    }

    @Override
    public boolean acceptsKey(OrchidPage page, String key) {
        return key.equals("category");
    }

    @Override
    public String format(OrchidPage page, String key) {
        if(page instanceof PostPage) {
            String category = ((PostPage) page).getCategory();
            return (category != null) ? category : "";
        }
        if(page instanceof PostArchivePage) {
            String category = ((PostArchivePage) page).getCategory();
            return (category != null) ? category : "";
        }

        return null;
    }

}