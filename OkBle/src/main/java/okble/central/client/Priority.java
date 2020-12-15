package okble.central.client;

public final class Priority implements Comparable<Priority> {

    public enum Level{
        HIGHEST(4),
        HIGH(3),
        NORMAL(2),
        LOW(1),
        LOWEST(0);
        private int val;
        Level(int val){
            this.val = val;
        }
        public int val(){
            return this.val;
        }
    }


    private Level level;
    private int value;


    private Priority(final Level level, final int value){
        this.level = level;
        this.value = value;
    }

    public int value(){
        return this.value;
    }

    public Level level(){
        return this.level;
    }

    public static Priority high(final int value){
        return new Priority(Level.HIGH, value);
    }
    public static Priority high(){
        return new Priority(Level.HIGH, 0);
    }

    public static Priority low(final int value){
        return new Priority(Level.LOW, value);
    }
    public static Priority low(){
        return new Priority(Level.LOW, 0);
    }

    public static Priority normal(final int value){
        return new Priority(Level.NORMAL, value);
    }

    public static Priority normal(){
        return new Priority(Level.NORMAL, 0);
    }

    static Priority highest(){
        return new Priority(Level.HIGHEST, Integer.MAX_VALUE);
    }
    static Priority highest(final int value){
        return new Priority(Level.HIGHEST, value);
    }

    static Priority lowest(){
        return new Priority(Level.LOWEST, Integer.MIN_VALUE);
    }
    static Priority lowest(final int value){
        return new Priority(Level.LOWEST, value);
    }

    @Override
    public int compareTo(Priority o) { 
        if(o == null){
            return 1;
        }
        final int val = o.level.val - this.level.val;
        if(val != 0){
            return val;
        }
        return o.value - this.value;
    }


    @Override
    public String toString() {
        return "Priority{" +
                "level=" + level +
                ", value=" + value +
                '}';
    }
}
