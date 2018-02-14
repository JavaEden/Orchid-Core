package com.eden.orchid.impl.themes.functions;

import com.eden.orchid.api.OrchidContext;
import com.eden.orchid.api.compilers.TemplateFunction;
import com.eden.orchid.api.converters.DateTimeConverter;
import com.eden.orchid.api.options.annotations.Option;
import com.eden.orchid.api.options.annotations.StringDefault;
import lombok.Getter;
import lombok.Setter;

import javax.inject.Inject;
import java.time.format.DateTimeFormatter;

@Getter @Setter
public final class LocalDateFunction extends TemplateFunction {

    private final OrchidContext context;
    private final DateTimeConverter converter;

    @Option @StringDefault("MMM d yyyy  hh:mm a")
    private String format;

    @Inject
    public LocalDateFunction(OrchidContext context, DateTimeConverter converter) {
        super("localDate");
        this.context = context;
        this.converter = converter;
    }

    @Override
    public String[] parameters() {
        return new String[] {"format"};
    }

    @Override
    public Object apply(Object input) {
        return DateTimeFormatter.ofPattern(format).format(converter.convert(input).second);
    }

}
