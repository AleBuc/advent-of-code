import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Advent0302 {
    public static void main(String[] args) {
/*        String schema = "";*/
        String schema = "";

        String[] lines = schema.split("\n");
        List<Number> numbers = new ArrayList<>();
        List<Symbol> symbols = new ArrayList<>();
        for (int lineIndex=0; lineIndex<lines.length; lineIndex++){
            numbers.addAll(getNumbers(lines[lineIndex], lineIndex));
            symbols.addAll(getSymbols(lines[lineIndex], lineIndex));
        }
        for(Symbol symbol : symbols) {
            List<Integer> impactedLines = List.of(symbol.line-1, symbol.line, symbol.line+1);
            List<Integer> impactedIndexes = List.of(symbol.index-1, symbol.index, symbol.index+1);
            numbers = numbers.stream()
                    .map(number -> {
                        if (impactedLines.contains(number.line) && impactedIndexes.stream().anyMatch(index -> number.indexes.contains(index))){
                            number.addSymbol(symbol);
                        }
                        return number;
                    })
                    .toList();
        }

        Map<Symbol, List<Number>> map = new HashMap<>();
        for (Number number: numbers){
            if (!number.symbols.isEmpty()){
                for (Symbol symbol: number.symbols){
                    if (map.containsKey(symbol)){
                        List<Number> numberList = map.get(symbol);
                        numberList.add(number);
                        map.replace(symbol, numberList);
                    } else {
                        map.put(symbol, new ArrayList<>(List.of(number)));
                    }
                }
            }
        }
        List< Map.Entry<Symbol, List<Number>>> entriesToMultiply = map.entrySet().stream()
                .filter(symbolListEntry -> symbolListEntry.getKey().symbol == '*' && symbolListEntry.getValue().size() == 2)
                .toList();
        int sum = entriesToMultiply.stream().map(entry -> entry.getValue().stream().map(Number::getNumber).reduce(1, (integer, integer2) -> integer*integer2)).reduce(0, Integer::sum);

        System.out.println(entriesToMultiply);
        System.out.println(sum);
    }

    static List<Number> getNumbers(String line, int lineNumber){
        List<Number> result = new ArrayList<>();
        for (int i = 0; i<line.length() ; i++) {
            if (Character.isDigit(line.charAt(i))){
                Number number = new Number();
                number.setLine(lineNumber);
                StringBuilder stringBuilder = new StringBuilder();
                List<Integer> indexes = new ArrayList<>();
                while (i<line.length() && Character.isDigit(line.charAt(i))){
                    stringBuilder.append(line.charAt(i));
                    indexes.add(i);
                    i++;
                }
                number.setNumber(Integer.parseInt(stringBuilder.toString()));
                number.setIndexes(indexes);
                result.add(number);
            }

        }
        return result;
    }

    static List<Symbol> getSymbols(String line, int lineNumber) {
        List<Symbol> result = new ArrayList<>();
        for (int i = 0; i<line.length() ; i++) {
            if (!Character.isLetterOrDigit(line.charAt(i)) && line.charAt(i) != '.'){
                Symbol symbol = new Symbol();
                symbol.setSymbol(line.charAt(i));
                symbol.setLine(lineNumber);
                symbol.setIndex(i);
                result.add(symbol);
            }
        }
        return result;
    }

    private static class Symbol {
        char symbol;
        int line;
        int index;

        public char getSymbol() {
            return symbol;
        }

        public void setSymbol(char symbol) {
            this.symbol = symbol;
        }

        public int getLine() {
            return line;
        }

        public void setLine(int line) {
            this.line = line;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }
    }

    private static class Number {
        int number;
        List<Symbol> symbols = new ArrayList<>();
        int line;
        List<Integer> indexes;

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public List<Symbol> getSymbols() {
            return symbols;
        }

        public void setSymbols(List<Symbol> symbols) {
            this.symbols = symbols;
        }
        public void addSymbol(Symbol symbol) {
            this.symbols.add(symbol);
        }

        public int getLine() {
            return line;
        }

        public void setLine(int line) {
            this.line = line;
        }

        public List<Integer> getIndexes() {
            return indexes;
        }

        public void setIndexes(List<Integer> indexes) {
            this.indexes = indexes;
        }
    }
}