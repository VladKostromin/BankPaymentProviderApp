package com.vladkostromin.bankpaymentproviderapp.beanpostprocessor;

import com.vladkostromin.bankpaymentproviderapp.annotation.RandomNumber;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Random;

@Component
public class RandomNumberAnnotationBeanPostProcessor implements BeanPostProcessor {

    private final Random random = new Random();

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        for(Field field : bean.getClass().getDeclaredFields()) {
            if(field.isAnnotationPresent(RandomNumber.class)) {
                RandomNumber randomNumber = field.getAnnotation(RandomNumber.class);
                int bound = randomNumber.bound();
                int randomValue = random.nextInt(bound);
                try {
                    field.setAccessible(true);
                    field.set(bean, randomValue);
                } catch (IllegalAccessException e) {
                    throw new BeansException("Failed to set random number for field: " + field.getName(), e) {

                    };
                }
            }
        }
        return bean;
    }
}
