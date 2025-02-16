class RegularExpression {
    private String pattern;
    
    //

    public RegularExpression(String pattern) {
        this.pattern = pattern;
    }

    public String getPattern() {
        return pattern;
    }

    public NFA toNFA() {
        // later
        return new NFA();
    }
}
