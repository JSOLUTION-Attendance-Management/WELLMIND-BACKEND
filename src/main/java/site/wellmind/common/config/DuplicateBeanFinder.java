package site.wellmind.common.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class DuplicateBeanFinder {

    private ApplicationContext context;

    public void findDuplicateBeans(){
        Map<Class<?>, List<String>> beanTypeMap=new HashMap<>();

        String[] beanNames=context.getBeanDefinitionNames();
        for (String beanName:beanNames){
            Class<?> beanType=context.getType(beanName);
            if(beanType!=null){
                beanTypeMap.putIfAbsent(beanType,new ArrayList<>());
                beanTypeMap
                        .get(beanType).add(beanName);
            }
        }

        beanTypeMap.forEach((type,names)->{
            if(names.size()>1){
                System.out.println("Duplicate beans of type: "+type.getName());
                names.forEach(name -> System.out.println("- " + name));
            }
        });
    }

}
