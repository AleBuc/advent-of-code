import java.util.ArrayList;
import java.util.List;

class Advent0201 {
    public static void main(String[] args) {
        String inventory = "";
        String[] lines = inventory.split("\n");
        List<Line> mappedLines = new ArrayList<>();
        for (String line : lines) {
            mappedLines.add(mapTheLine(line));
        }
        int givenRed = Integer.parseInt(args[0]);
        int givenGreen = Integer.parseInt(args[1]);
        int givenBlue = Integer.parseInt(args[2]);

        List<String> filteredIdList = mappedLines.stream()
                .filter(line -> line.getDetails().stream()
                                .allMatch(detail -> detail.getRed() <= givenRed && detail.getGreen() <= givenGreen && detail.getBlue() <= givenBlue)
                )
                .map(Line::getId)
                .toList();

        Integer sum = filteredIdList.stream()
                .map(Integer::parseInt)
                .reduce(0, Integer::sum);

        System.out.println(filteredIdList);
        System.out.println(sum);
    }

    private static Line mapTheLine(String line) {
        Line mappedLine = new Line();
        String[] split = line.split(": ");
        String id = split[0].replaceAll("[^\\d.]", "");
        mappedLine.setId(id);
        String definition = split[1];
        String[] definitionDetails = definition.split(";");
        List<Detail> details = new ArrayList<>();
        for (String detailString : definitionDetails) {
            Detail detail = new Detail();
            String[] colorDetails = detailString.split(",");
            for (String colorDetail : colorDetails) {
                String detailNumber = colorDetail.replaceAll("[^\\d.]", "");
                if (colorDetail.contains("red")) {
                    detail.setRed(Integer.parseInt(detailNumber));
                }
                if (colorDetail.contains("green")) {
                    detail.setGreen(Integer.parseInt(detailNumber));
                }
                if (colorDetail.contains("blue")) {
                    detail.setBlue(Integer.parseInt(detailNumber));
                }
            }
            details.add(detail);
        }
        mappedLine.setDetails(details);
        return mappedLine;
    }

    private static class Line {
        private String id;
        private List<Detail> details;

        public String getId() {
            return id;
        }

        public List<Detail> getDetails() {
            return details;
        }

        public void setId(String id) {
            this.id = id;
        }

        public void setDetails(List<Detail> details) {
            this.details = details;
        }
    }

    private static class Detail {
        private int red;
        private int green;
        private int blue;

        public int getRed() {
            return red;
        }

        public void setRed(int red) {
            this.red = red;
        }

        public int getGreen() {
            return green;
        }

        public void setGreen(int green) {
            this.green = green;
        }

        public int getBlue() {
            return blue;
        }

        public void setBlue(int blue) {
            this.blue = blue;
        }
    }

}