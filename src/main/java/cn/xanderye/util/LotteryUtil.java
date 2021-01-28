package cn.xanderye.util;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created on 2021/1/28.
 * 抽奖工具类，支持按概率抽和按总数抽
 *
 * @author XanderYe
 */
public class LotteryUtil {

    /**
     * 是否抽一次减少一次奖品
     */
    private boolean isReduce = true;

    /**
     * 奖品列表
     */
    private List<Award> awardList = new CopyOnWriteArrayList<>();

    /**
     * 没抽中的奖品
     */
    private Award sp;

    /**
     * 统计总概率校验用
     */
    private double totalProbability = 0;

    /**
     * 随机数生成器
     */
    private final Random random = new Random();


    public LotteryUtil() {
    }

    public LotteryUtil(boolean isReduce) {
        this.isReduce = isReduce;
    }

    /**
     * 按概率抽奖
     * @return java.lang.String
     * @author XanderYe
     * @date 2021/1/28
     */
    public synchronized String lotteryByProbability() {
        double r = random.nextDouble();
        Award awdRes = null;
        for (Award award : awardList) {
            r -= award.getProbability(isReduce);
            if (r < 0) {
                awdRes = award;
                break;
            }
        }
        if (awdRes != null) {
            if (isReduce) {
                awdRes.setNum(awdRes.getNum() - 1);
            }
        } else {
            awdRes = sp;
        }
        return awdRes == null ? "没抽中" : awdRes.getName();
    }

    /**
     * 按数量抽奖
     * @return void
     * @author XanderYe
     * @date 2021/1/28
     */
    public synchronized String lotteryByNum() {
        int totalNum = awardList.stream().mapToInt(Award::getNum).sum();
        Award awdRes = null;
        if (totalNum > 0) {
            int r = random.nextInt(totalNum);
            for (Award award : awardList) {
                r -= award.getNum();
                if (r < 0) {
                    awdRes = award;
                    break;
                }
            }
            if (awdRes != null) {
                if (isReduce) {
                    awdRes.setNum(awdRes.getNum() - 1);
                }
            }
        }
        return awdRes == null ? "没抽中" : awdRes.getName();
    }

    /**
     * 重置奖品列表
     * @param
     * @return void
     * @author XanderYe
     * @date 2021/1/28
     */
    public void reset() {
        awardList = new CopyOnWriteArrayList<>();
    }

    /**
     * 增加奖品
     * @param name 奖品名称
     * @param weight 奖品权重 [0, 1]
     * @param num 奖品数量 (开启减少奖品功能才有用)
     * @return void
     * @author XanderYe
     * @date 2021/1/28
     */
    public void addAward(String name, double weight, int num) {
        Award award = new Award(name, weight, num);
        if (totalProbability + award.getProbability(isReduce) > 1) {
            throw new RuntimeException("概率超出");
        }
        if (num == 0) {
            sp = award;
        }
        awardList.add(award);
        totalProbability += award.getProbability(isReduce);
    }

    /**
     * 删除奖品
     * @param name
     * @return void
     * @author XanderYe
     * @date 2021/1/28
     */
    public void removeAward(String name) {
        for (Award award : awardList) {
            if (name.equals(award.getName())) {
                totalProbability -= award.getProbability(isReduce);
                awardList.remove(award);
            }
        }
    }

    /**
     * 获取奖品列表
     * @param
     * @return java.util.List<cn.xanderye.util.LotteryUtil.Award>
     * @author XanderYe
     * @date 2021/1/28
     */
    public List<Award> getAwardList() {
        return awardList;
    }

    /**
     * 奖品对象
     */
    public static class Award {
        private String name;

        private Double weight;

        private Integer num;

        public Award(String name, Double weight) {
            this.name = name;
            this.weight = weight;
        }

        Award(String name, Double weight, Integer num) {
            this.name = name;
            this.weight = weight;
            this.num = num;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Double getWeight() {
            return weight;
        }

        public void setWeight(Double weight) {
            this.weight = weight;
        }

        public Integer getNum() {
            return num;
        }

        public void setNum(Integer num) {
            this.num = num;
        }

        /**
         * 获取概率
         * @param isReduce 抽一次减少一次奖品
         * @return double
         * @author XanderYe
         * @date 2021/1/28
         */
        public double getProbability(boolean isReduce) {
            return isReduce ? weight * num : weight;
        }
    }
}
