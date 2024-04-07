/*Business Use Case: Your organization sells products to its customers through opportunities. 
Each opportunity can have multiple products (Opportunity Line Items) associated with it. The organization wants to 
keep track of the total number of products sold to each account and display it on the Account record for reporting purposes.

Pre Work: Create a custom field on the Account object named Number_of_Products__c (Number) 
to count the total number of products related to all Opportunities associated with the Account.*/

// Action occur on -- OpportunityLineItem
// Event -- After
// Operation -- Insert,Delete


trigger OpportunityLineItemTrigger on OpportunityLineItem (After insert,After Delete) {
    new Opportunitylineitemhandler().doAction();
   
}

// Trigger Handler
public class Opportunitylineitemhandler {
    List<OpportunityLineItem> TriggerNew ;
    List<OpportunityLineItem> Triggerold ;
    Map<Id,OpportunityLineItem> TriggerNewmap;
    Map<Id,OpportunityLineItem> TriggerOldmap;
    
    Public Opportunitylineitemhandler(){
        TriggerNew =(List<OpportunityLineItem>) Trigger.new;
        Triggerold =(List<OpportunityLineItem>) Trigger.old;
        TriggerNewmap=(Map<Id,OpportunityLineItem>) Trigger.newmap;
        TriggerOldmap=(Map<Id,OpportunityLineItem>) Trigger.oldmap;
    }
    Public void doAction(){
        Switch on Trigger.Operationtype{
            When BEFORE_INSERT
            {
                
            }
            When BEFORE_UPDATE{
                
            }
            When AFTER_INSERT{
                countupdateonAccount(TriggerNew);
            }
            When AFTER_UPDATE{
                
            }
            When AFTER_DELETE{
                 countupdateonAccount(Triggerold);
            }
        }
        
    }
    
    Public  void countupdateonAccount(List<OpportunityLineItem> Items){
       OpportunityLineItemHelper.CountUpdateHelper(Items);
        
    }
    
    
}


// Trigger Helper
public class OpportunityLineItemHelper {
    
    Public static void CountUpdateHelper(List<OpportunityLineItem> OppItem){
        set<Id> OpportunityIds = New set<Id>();
        for(OpportunityLineItem records : OppItem){
            if(!OpportunityLineItemRecurrsion.setOfIds.contains(records.Id)){
                OpportunityLineItemRecurrsion.setOfIds.add(records.Id);
                OpportunityIds.add(records.OpportunityId);
            }
        }
        
        If(OpportunityIds.size()>0){
            
            
            List<Opportunity> OppwithAcc =   [Select AccountId ,(Select Id From  OpportunityLineItems)                            
                                              From Opportunity Where Id In:OpportunityIds
                                              and AccountId!=Null];
            
            
            
            List<Account> ProcessList =new List<Account>();
            for(Opportunity records : OppwithAcc){
                Account Empty = new Account();
                Empty.Id = records.AccountId ;
                Empty.Number_of_Product__c = records.OpportunityLineItems.size()  ;
                ProcessList.add(Empty);
            }
            if(ProcessList.size()>0){
                Update ProcessList;
            }
        }
    }
    
}

// Trigger Recursin Handling
public class OpportunityLineItemRecurrsion {
    Public static Set<Id> setOfIds = new Set<Id>();

}