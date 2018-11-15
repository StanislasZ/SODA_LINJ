package com.cisco.fcp.log_websocket;

//@Service
//public class ProcessLogFilter extends Filter<ILoggingEvent> {
//
//    @Override
//    public FilterReply decide(ILoggingEvent event) {
//        LoggerMessage loggerMessage = new LoggerMessage(
//                event.getMessage()
//                , DateFormat.getDateTimeInstance().format(new Date(event.getTimeStamp())),
//                event.getThreadName(),
//                event.getLoggerName(),
//                event.getLevel().levelStr
//        );
//        LoggerDisruptorQueue.publishEvent(loggerMessage);
//        return FilterReply.ACCEPT;
//    }
//}