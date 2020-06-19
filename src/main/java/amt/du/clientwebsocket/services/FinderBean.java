package amt.du.clientwebsocket.services;

import amt.du.clientwebsocket.mdb.ImmediatePaymentMessageListerner;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class FinderBean {
     private Set<ImmediatePaymentMessageListerner> allPrototypeTypeBeans = new HashSet<>();

     public void register(ImmediatePaymentMessageListerner beanToRegister) {
          this.allPrototypeTypeBeans.add(beanToRegister);
     }
     
     public Set<ImmediatePaymentMessageListerner> getAllPrototypeTypeBeans(){
    	 return allPrototypeTypeBeans;
     }
}