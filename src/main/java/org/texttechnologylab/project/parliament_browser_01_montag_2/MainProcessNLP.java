package org.texttechnologylab.project.parliament_browser_01_montag_2;

import org.texttechnologylab.project.parliament_browser_01_montag_2.data.Rede;
import org.texttechnologylab.project.parliament_browser_01_montag_2.data.impl.factory.InsightBundestagFactory;
import org.texttechnologylab.project.parliament_browser_01_montag_2.data.impl.factory.InsightBundestagFactory_Impl;
import org.texttechnologylab.project.parliament_browser_01_montag_2.database.MongoDBHandler;

import java.util.Iterator;
import java.util.Set;

/**
 * Class for importing the speeches and processing them using NLP
 * @author Mihai Paun
 */
public class MainProcessNLP {
    public static InsightBundestagFactory bundestagFactory;
    public static MongoDBHandler dbHandler;

    public static void main(String[] args) throws Exception {
        // init the factory and handler
        bundestagFactory = new InsightBundestagFactory_Impl();
        System.out.println("Factory initiated! ");
        dbHandler = new MongoDBHandler();
        System.out.println("MongoDB connected! ");

        try{
            int batchSize = 200; // we will process 300 speeches at a time
            int progress = 0; // used to keep track of the progress made
            int count = 0;

            while (true){
                Set<Rede> redeBatch = bundestagFactory.getRestRedenBatchFromDB(0, 100);

                // exit the loop if no processing needed
                if (redeBatch.isEmpty()){
                    System.out.println("Batch is empty! ");
                    break;
                }

                // otherwise process the speeches
                Iterator<Rede> redeIterator = redeBatch.iterator();
                while (redeIterator.hasNext()){
                    Rede rede = redeIterator.next();
                    try {
                        count++;
                        System.out.println(count + ": " + rede.getId());
                        dbHandler.updateRede(rede);
                        redeIterator.remove();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }

//                for (Rede rede : redeBatch){
//                    try{
//                        count++;
//                        System.out.println(count + ": " + rede.getId());
//                        dbHandler.updateRede(rede);
//                        redeBatch.remove(rede);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
                redeBatch = null; // set the batch to null to free memory

                progress += batchSize; // update the progress
                // compute and print the progress made
                // implement later
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
