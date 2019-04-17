package fr.aquazus.diva.common.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;
import fr.aquazus.diva.common.DivaConfiguration;

public class DebugFilter extends Filter<ILoggingEvent> {

    @Override
    public FilterReply decide(ILoggingEvent event) {
        if (Level.DEBUG.isGreaterOrEqual(event.getLevel()) && !DivaConfiguration.debug) {
            return FilterReply.DENY;
        }
        return FilterReply.NEUTRAL;
    }
}
