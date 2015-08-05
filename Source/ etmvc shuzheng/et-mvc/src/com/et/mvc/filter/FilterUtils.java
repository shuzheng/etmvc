package com.et.mvc.filter;

import com.et.mvc.Controller;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

/**
 * 过滤器管理实用类
 * @author stworthy
 */
public class FilterUtils {
    private static boolean isExistsBeforeFilter(List<Filter> filters, BeforeFilter beforeFilter){
        for(Filter filter: filters){
            if (filter.getBeforeFilter() != null && filter.getBeforeFilter().execute().equals(beforeFilter.execute())){
                return true;
            }
        }
        return false;
    }
    
    private static boolean isExistsAfterFilter(List<Filter> filters, AfterFilter afterFilter){
        for(Filter filter: filters){
            if (filter.getAfterFilter() != null && filter.getAfterFilter().execute().equals(afterFilter.execute())){
                return true;
            }
        }
        return false;
    }
    
    private static boolean isExistsAroundFilter(List<Filter> filters, AroundFilter aroundFilter){
        for(Filter filter: filters){
            if (filter.getAroundFilter() != null && filter.getAroundFilter().execute().equals(aroundFilter.execute())){
                return true;
            }
        }
        return false;
    }
    
    public static List<Filter> getFilterChain(Controller controller) throws Exception{
        List<Filter> chains = new ArrayList<Filter>();
        Class<?> clasz = controller.getClass();
        while(!clasz.equals(Controller.class)){
            for(Annotation annotation: clasz.getAnnotations()){
                if (annotation instanceof BeforeFilter){
                    BeforeFilter beforeFilter = (BeforeFilter)annotation;
                    if (!isExistsBeforeFilter(chains, beforeFilter)){
                        Filter filter = new Filter(clasz, beforeFilter);
                        chains.add(filter);
                    }
                }
                else if (annotation instanceof BeforeFilters){
                    BeforeFilters beforeFilters = (BeforeFilters)annotation;
                    for(BeforeFilter bf: beforeFilters.value()){
                        if (!isExistsBeforeFilter(chains, bf)){
                            Filter filter = new Filter(clasz, bf);
                            chains.add(filter);
                        }
                    }
                }
                else if (annotation instanceof AfterFilter){
                    AfterFilter afterFilter = (AfterFilter)annotation;
                    if (!isExistsAfterFilter(chains, afterFilter)){
                        Filter filter = new Filter(clasz, afterFilter);
                        chains.add(filter);
                    }
                }
                else if (annotation instanceof AfterFilters){
                    AfterFilters afterFilters = (AfterFilters)annotation;
                    for(AfterFilter af: afterFilters.value()){
                        if (!isExistsAfterFilter(chains, af)){
                            Filter filter = new Filter(clasz, af);
                            chains.add(filter);
                        }
                    }
                }
                else if (annotation instanceof AroundFilter){
                    AroundFilter aroundFilter = (AroundFilter)annotation;
                    if (!isExistsAroundFilter(chains, aroundFilter)){
                        Filter filter = new Filter(clasz, aroundFilter);
                        filter.setAroundInstance(aroundFilter.execute().newInstance());
                        chains.add(filter);
                    }
                }
                else if (annotation instanceof AroundFilters){
                    AroundFilters aroundFilters = (AroundFilters)annotation;
                    for(AroundFilter af: aroundFilters.value()){
                        if (!isExistsAroundFilter(chains, af)){
                            Filter filter = new Filter(clasz, af);
                            filter.setAroundInstance(af.execute().newInstance());
                            chains.add(filter);
                        }
                    }
                }
            }
            clasz = clasz.getSuperclass();
        }
        return chains;
    }
}
