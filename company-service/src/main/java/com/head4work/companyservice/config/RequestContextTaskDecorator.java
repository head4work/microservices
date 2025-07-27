package com.head4work.companyservice.config;

import org.springframework.core.task.TaskDecorator;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

public class RequestContextTaskDecorator implements TaskDecorator {

    @Override
    public Runnable decorate(Runnable runnable) {
        // Capture RequestAttributes from the current thread (the incoming request thread)
        RequestAttributes context = RequestContextHolder.getRequestAttributes();
        return () -> {
            try {
                // Set the captured RequestAttributes on the new async thread
                if (context != null) {
                    RequestContextHolder.setRequestAttributes(context);
                }
                runnable.run();
            } finally {
                // Clear the RequestAttributes from the thread when done
                RequestContextHolder.resetRequestAttributes();
            }
        };
    }

}