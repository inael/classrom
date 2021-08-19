
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;
import java.util.stream.Collectors;


/**
 * It will be necessary to increase the amount of elements very often, so the ideal is to use the Vector that
 * increases twice and will have much more space than the Synchronized ArrayList (Collections.synchronizedList
 * (new ArrayList<>())) that will need to be increasing more often,
 * thus decreasing the application's performance.
 */
@Getter
public class BlockChain {
    
    private Vector<Block> blockList = new Vector<>();
    Map<String, Integer> reportSumOfBlocksPerMiner = new HashMap<String, Integer>();

    private int difficultyMineBlock;

    public BlockChain(int difficultyMineBlock) {

        this.difficultyMineBlock = difficultyMineBlock;
        this.addGenesisBlock();
    }

    public synchronized void addBlock(Block newBlock, String miner) {

        Block lastBlock = blockList.get(blockList.size() - 1);

        print(newBlock, miner, TextConstants.MINER_TRYING_ADD_NEW_BLOCK);

        if (isValidBlock(newBlock, lastBlock)) {
            print(newBlock, miner, TextConstants.MINER_ADDING_NEW_BLOCK_S);

            blockList.add(newBlock);

            updateReport(miner);

            print(newBlock, miner, TextConstants.MINER_ADICIONOU_O_BLOCK);
        } else {
            print(newBlock, miner, TextConstants.BLOCK_IS_INVALID);
        }
    }

    public void printReportSumOfBlocksPerMiner() {
        print("---------------Report Sum Of Blocks Per Miner----------------------");

        List<Map.Entry<String, Integer>> sortedByKey = reportSumOfBlocksPerMiner
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toList());

        sortedByKey.forEach(entry ->
                print(String
                        .format(TextConstants.SUM_OF_BLOCKS_PER_MINER, entry.getKey(), entry.getValue()))
        );

        print("-------------------------------------");
    }

    public  Vector<Block> getBlockList() {
        return blockList;
    }

    public void addGenesisBlock() {
        Block genesis = new Block("\"Starts from here!\"", "0");
        blockList.add(genesis);
        updateReport("GENESIS");
    }

    public int getDifficultyMineBlock() {
        return this.difficultyMineBlock;
    }

    public Boolean isChainValid() {
        synchronized (this) {
            String hashTarget = new String(new char[difficultyMineBlock]).replace('\0', '0');

            //loop through blockchain to check hashes:
            for (int i = 1; i < blockList.size(); i++) {
               Block currentBlock = blockList.get(i);
                Block previousBlock = blockList.get(i - 1);
                //compare registered hash and calculated hash:
                if (!currentBlock.getHash().equals(currentBlock.calculateHash())) {
                   print("Current Hashes not equal");
                    return false;
                }
                //compare previous hash and registered previous hash
                if (!previousBlock.getHash().equals(currentBlock.getPreviousHash())) {
                   print("Previous Hashes not equal");
                    return false;
                }
                //check if hash is solved
                if (!currentBlock.getHash().substring(0, difficultyMineBlock).equals(hashTarget)) {
                   print("This block hasn't been mined");
                    return false;
                }
            }
            return true;
        }
    }

    public Block getLastBlock() {
        int lastBlockIndex = blockList.size() - 1;
        return blockList.get(lastBlockIndex);
    }

    private void print(Block newBlock, String miner, String minerAddingNewBlockS) {
        print(String.format(minerAddingNewBlockS, miner,
                newBlock.getData()));
    }

    private void print(String msg){
        System.out.println(msg);
    }
    private  boolean isValidBlock(Block newBlock, Block lastBlock) {
        return lastBlock.getHash().equals(newBlock.getPreviousHash()) && newBlock.getHash().equals(newBlock.calculateHash());
    }

    private  void updateReport(String miner) {
        reportSumOfBlocksPerMiner.merge(miner, 1, (v1, v2) -> v1 + v2);;
//  ------------------------      Option 1 - JAVA 8-------------------------
//        reportSumOfBlocksPerMiner.compute(miner.getName(), (k, v) -> (v == null) ? new Integer(1) : v.intValue() + 1);
//
//        ------------------------Option 2 - JAVA7------------------------
//        Integer blockSum = new Integer(0);
//        if (reportSumOfBlocksPerMiner.containsKey(miner.getName())) {
//            blockSum = this.reportSumOfBlocksPerMiner.get(miner.getName());
//        }
//        this.reportSumOfBlocksPerMiner.put(miner.getName(), new Integer(blockSum + 1));
//        //------------------------Option 3 - JAVA8------------------------
//        Integer blockSum = reportSumOfBlocksPerMiner.getOrDefault(miner.getName(), new Integer(0));
//        this.reportSumOfBlocksPerMiner.put(miner.getName(), new Integer(blockSum.intValue() + 1));
    }
}
