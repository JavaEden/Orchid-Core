package com.eden.orchid.posts;

import com.eden.orchid.api.options.annotations.Option;
import com.eden.orchid.api.options.OptionsHolder;
import com.eden.orchid.api.options.annotations.IntDefault;


public class PostsPaginator implements OptionsHolder {

    @Option
    @IntDefault(100)
    public int pageSize;

    @Option
    public String permalink;

}