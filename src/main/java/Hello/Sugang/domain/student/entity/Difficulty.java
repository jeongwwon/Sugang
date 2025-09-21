package Hello.Sugang.domain.student.entity;

public enum Difficulty {

    EASY(800, 1200),     // 빠른 클릭
    MEDIUM(300, 700),   // 보통
    HARD(100, 200);    // 느림

    private final int minDelay;
    private final int maxDelay;

    Difficulty(int minDelay, int maxDelay) {
        this.minDelay = minDelay;
        this.maxDelay = maxDelay;
    }

    public int getRandomDelay() {
        return minDelay + (int)(Math.random() * (maxDelay - minDelay + 1));
    }

}
