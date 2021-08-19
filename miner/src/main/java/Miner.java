import lombok.Getter;

import java.util.UUID;

@Getter
public class Miner implements Runnable {


    private String name;
    private BlockChain blockChain;
    private Integer blockNumber;

    public Miner(int id, BlockChain blockChain, Integer blockNumber) {
        this.name = new Integer(id).toString();
        this.blockChain = blockChain;
        this.blockNumber = blockNumber;
    }

    @Override
    public void run() {
        int difficulty = blockChain.getDifficultyMineBlock();
        for (int i = 0; i < blockNumber; i++) {

            String blockName = name + "-" + UUID.randomUUID().toString().substring(0, 4);
            Block lastBlock = blockChain.getLastBlock();

            Block newBlock = new Block(blockName, lastBlock.getHash());
            print(String.format(TextConstants.START_MINING, name));
            newBlock.mineBlock(difficulty);
            print(String.format(TextConstants.END_MINING, name));
            blockChain.addBlock(newBlock, this.getName());

        }
    }

    private void print(String msg) {
        System.out.println(msg);
    }
}
