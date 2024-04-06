public class OpportunityTriggerHandler {
    
    List<Opportunity> TriggerNew ;
    List<Opportunity> TriggerOld ;
    Map<Id,Opportunity> TriggerNewmap ;
    Map<Id,Opportunity> TriggerOldmap ;
    
    Public OpportunityTriggerHandler(){
        TriggerNew =(List<Opportunity>) Trigger.New ;
        TriggerOld =(List<Opportunity>) Trigger.old ;
        TriggerNewmap = (Map<Id,Opportunity>) Trigger.Newmap ;
        TriggerOldmap = (Map<Id,Opportunity>) Trigger.oldmap ;
    }
    
    Public void doAction(){
        Switch on Trigger.Operationtype{
            When BEFORE_INSERT{
                
            }
            When AFTER_INSERT{
               UpdateOppAmountonAccount(TriggerNew);
            }
            When AFTER_UPDATE{
               UpdateOppAmountonAccount(TriggerNew);
            }
            When AFTER_DELETE{
                UpdateOppAmountonAccount(TriggerOld);
            }
        }
    }
    
    Public void UpdateOppAmountonAccount(List<Opportunity> TriggerList){
        set<Id> AccountIds = new set<Id>();
        
        for(Opportunity records : TriggerList){
            if(records.AccountId!=Null && records.Amount!=Null && Trigger.isInsert && 
               (records.StageName!='Closed Won' || records.StageName!='Closed Lost')){
                   AccountIds.add(records.AccountId);
               }
            else if(Trigger.isUpdate && ((records.Amount!= TriggerOldmap.get(records.Id).Amount) || 
                                         (records.StageName!= TriggerOldmap.get(records.Id).StageName) ) ) {
                                             AccountIds.add(records.AccountId);
                                         }
            else if(Trigger.isdelete && records.AccountId!=Null && records.Amount!=Null){
                AccountIds.add(records.AccountId);
            }
        }
        
        If(AccountIds.size()>0){
            Map<Id,AggregateResult> AccountwithOppAmount  = new Map<Id,AggregateResult>([SELECT AccountId Id ,SUM(Amount) TotalAmount
                                                                                         From Opportunity 
                                                                                         Where 
                                                                                         StageName Not in ('Closed Won','Closed Lost')
                                                                                         and
                                                                                         AccountId in:AccountIds
                                                                                         Group by AccountId]);
            
            
            
            List<Account> ProcessedAccount = new List<Account>();
            for(string keys :AccountIds ){
                
                Account Empty = new Account();
                Empty.Id = keys ;
                If(AccountwithOppAmount.containskey(keys)){
                    Empty.AnnualRevenue = (Decimal) AccountwithOppAmount.get(keys).get('TotalAmount');
                }
                else{
                    Empty.AnnualRevenue = 0;
                }
                ProcessedAccount.add(Empty);
            }
            
            If(ProcessedAccount.size()>0){
                Update ProcessedAccount;
            }
            
        }
    }
    
    
}