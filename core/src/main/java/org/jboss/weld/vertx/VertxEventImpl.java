package org.jboss.weld.vertx;

import io.vertx.core.MultiMap;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

class VertxEventImpl implements VertxEvent {

    private static final Logger LOGGER = LoggerFactory.getLogger(VertxEventImpl.class.getName());

    private final EventBus eventBus;

    private final Message<Object> message;

    private Object reply;

    VertxEventImpl(Message<Object> message, EventBus eventBus) {
        this.eventBus = eventBus;
        this.message = message;
    }

    @Override
    public String getAddress() {
        return message.address();
    }

    @Override
    public MultiMap getHeaders() {
        return message.headers();
    }

    @Override
    public Object getMessageBody() {
        return message.body();
    }

    @Override
    public String getReplyAddress() {
        return message.replyAddress();
    }

    @Override
    public boolean setReply(Object reply) {
        if (message.replyAddress() == null) {
            LOGGER.warn("The message was sent without a reply handler - the reply will be ignored");
        }
        if (this.reply != null) {
            LOGGER.warn("A reply was already set - the old value is replaced");
            return false;
        }
        this.reply = reply;
        return true;
    }

    @Override
    public boolean isReplied() {
        return reply != null;
    }

    @Override
    public void fail(int code, String message) {
        throw new RecipientFailure(code, message);
    }

    @Override
    public VertxMessage messageTo(String address) {
        return new VertxMessageImpl(address, eventBus);
    }

    Object getReply() {
        return reply;
    }

}