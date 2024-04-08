/*Create a trigger that prevents the deletion of an Account record if it is associated with any Contact

Action Occur on  - Account 
Event -  Before 
Action - Delete */

trigger AccountTrigger on Account (Before delete,After delete) {
    new AccountHandler().doAction();

}

public class AccountHandler {
    
    List<Account> TriggerNew ;
    List<Account> TriggerOld ;
    Map<Id,Account> TriggerNewmap ;
    Map<Id,Account> TriggerOldmap ;
    
    Public AccountHandler(){
        TriggerNew  = (List<Account>) Trigger.new ;
        TriggerOld  =  (List<Account>) Trigger.old ;
        TriggerNewmap = (Map<Id,Account>) Trigger.newmap ;
        TriggerOldmap = (Map<Id,Account>)  Trigger.oldmap ;
    }
    
    Public void doAction(){
        Switch on Trigger.operationtype{
            When BEFORE_INSERT{
                
            }
            When AFTER_INSERT{
                
            }
            
            When AFTER_UPDATE{
                
            }
            When BEFORE_DELETE{
                PreventDeletion();
            }
            
        }
    }
    Public void  PreventDeletion(){
        
    // Collection to store the IDs of accounts associated with contacts
    Set<Id> accountIdsWithContacts = new Set<Id>();
    
    // Query to find accounts associated with contacts
    for(Contact con : [SELECT AccountId FROM Contact WHERE AccountId IN :TriggerOld]) {
        accountIdsWithContacts.add(con.AccountId);
    }
    
    //Then add Error to the record 
   for(Id record : accountIdsWithContacts){
            TriggerOldmap.get(record ).addError('no');
        }
    
    }
    
}