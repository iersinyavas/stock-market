package com.artsoft.stock.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.item.adapter.AbstractMethodInvokingDelegator;
import org.springframework.batch.item.adapter.DynamicMethodInvocationException;
import org.springframework.batch.item.support.AbstractItemCountingItemStreamItemReader;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.Repository;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.MethodInvoker;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ListItemReader<T,R> extends AbstractItemCountingItemStreamItemReader<T> implements InitializingBean {

    protected Log logger = LogFactory.getLog(getClass());

    private Repository<?, ?> repository;

    private int pageSize = 10;

    private volatile int page = 0;

    private Function<T, R> criteriaFunction;

    private volatile T lastObjectOfCursor = null;

    private volatile int current = 0;

    private List<?> arguments;

    private volatile List<T> results;

    private final Object lock = new Object();

    private String methodName;

    public ListItemReader() {
        setName(ClassUtils.getShortName(ListItemReader.class));
    }

    public Function<T, R> getCriteriaFunction() {
        return criteriaFunction;
    }

    public void setCriteriaFunction(Function<T, R> criteriaFunction) {
        this.criteriaFunction = criteriaFunction;
    }

    /**
     * Arguments to be passed to the data providing method.
     *
     * @param arguments list of method arguments to be passed to the repository
     */
    public void setArguments(List<?> arguments) {
        this.arguments = arguments;
    }

    /**
     * @param pageSize The number of items to retrieve per page.
     */
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * The {@link Repository}
     * implementation used to read input from.
     *
     * @param repository underlying repository for input to be read from.
     */
    public void setRepository(Repository<?, ?> repository) {
        this.repository = repository;
    }

    /**
     * Specifies what method on the repository to call.  This method must take
     * {@link Pageable} as the <em>last</em> argument.
     *
     * @param methodName name of the method to invoke
     */
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.state(repository != null, "A PagingAndSortingRepository is required");
        Assert.state(pageSize > 0, "Page size must be greater than 0");
    }

    @Nullable
    @Override
    protected T doRead() throws Exception {

        synchronized (lock) {
            boolean nextPageNeeded = (results != null && current >= results.size());

            if (results == null || nextPageNeeded) {

                results = doPageRead();

                page++;

                if(results.size() <= 0) {
                    return null;
                }

                if (nextPageNeeded) {
                    current = 0;
                }
            }

            if(current < results.size()) {
                T curLine = results.get(current);
                lastObjectOfCursor = curLine;
                current++;
                return curLine;
            }
            else {
                return null;
            }


        }
    }

    @Override
    protected void jumpToItem(int itemLastIndex) throws Exception {
        synchronized (lock) {
            current = itemLastIndex % pageSize;
        }
    }

    /**
     * Performs the actual reading of a page via the repository.
     * Available for overriding as needed.
     *
     * @return the list of items that make up the page
     * @throws Exception Based on what the underlying method throws or related to the
     * 			calling of the method
     */
    @SuppressWarnings("unchecked")
    protected List<T> doPageRead() throws Exception {
        MethodInvoker invoker = createMethodInvoker(repository, methodName);

        List<Object> parameters = new ArrayList<>();

        if (arguments != null && arguments.size() > 0) {
            parameters.addAll(arguments);
        }

        R criteriaValue = criteriaFunction.apply(lastObjectOfCursor);
        logger.info("Current Page :" + String.valueOf(page));
        logger.info("Criteria Value :" + String.valueOf(criteriaValue));
        parameters.add(criteriaValue);
        parameters.add(pageSize);

        invoker.setArguments(parameters.toArray());

        List<T> curPage = (List<T>) doInvoke(invoker);

        return curPage;
    }

    @Override
    protected void doOpen() throws Exception {
        /*
        Burasinin bos birakiyoruz, neden bilmiyorum ....
         */
    }

    @Override
    protected void doClose() throws Exception {
        synchronized (lock) {
            current = 0;
            results = null;
        }
    }

    private Object doInvoke(MethodInvoker invoker) throws Exception{
        try {
            invoker.prepare();
        }
        catch (ClassNotFoundException | NoSuchMethodException e) {
            throw new DynamicMethodInvocationException(e);
        }

        try {
            return invoker.invoke();
        }
        catch (InvocationTargetException e) {
            if (e.getCause() instanceof Exception) {
                throw (Exception) e.getCause();
            }
            else {
                throw new AbstractMethodInvokingDelegator.InvocationTargetThrowableWrapper(e.getCause());
            }
        }
        catch (IllegalAccessException e) {
            throw new DynamicMethodInvocationException(e);
        }
    }

    private MethodInvoker createMethodInvoker(Object targetObject, String targetMethod) {
        MethodInvoker invoker = new MethodInvoker();
        invoker.setTargetObject(targetObject);
        invoker.setTargetMethod(targetMethod);
        return invoker;
    }
}
