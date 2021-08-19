import com.google.gson.GsonBuilder;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class Application {


    public static void main(String[] args) throws InterruptedException {


        Scanner sc = new Scanner(System.in);

        print(TextConstants.ENTER_THE_NUMBER_OF_THREADS);
        Integer threadNumber = new Integer(sc.next());


        print(TextConstants.ENTER_THE_NUMBER_OF_BLOCKS_PER_MINER);
        Integer blockNumber = new Integer(sc.next());

        print(TextConstants.ENTER_A_NUMBER_TO_REPRESENT_THE_MINING_DIFFICULTY_EG_1_1000);
        Integer difficultyMineBlock = new Integer(sc.next());

        ExecutorService executorService = Executors.newFixedThreadPool(threadNumber);

        BlockChain blockChain = new BlockChain(difficultyMineBlock);

        long start = System.currentTimeMillis();

        createRunMiners(threadNumber, blockNumber, executorService, blockChain);

        executorService.shutdown();

        executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);


        String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockChain.getBlockList());
        print(String.format(TextConstants.THE_BLOCK_CHAIN, blockchainJson));

        long finish = System.currentTimeMillis();
        print(String.format(TextConstants.TIME_ELAPSED_TO_END_MINERS, finish - start));
        print(String.format(TextConstants.BLOCKCHAIN_IS_VALID, blockChain.isChainValid()));
        blockChain.printReportSumOfBlocksPerMiner();
    }

    private static void print(String msg) {
        System.out.println(msg);
    }

    private static void createRunMiners(Integer threadNumber, Integer blockNumber, ExecutorService executorService,
                                        BlockChain blockChain) {
        IntStream.range(0, threadNumber).forEach(i -> {
            Miner miner = new Miner(i, blockChain, blockNumber);
            executorService.submit(miner);
        });
    }
}
