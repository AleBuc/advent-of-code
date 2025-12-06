import java.util.ArrayList;
import java.util.List;

class Advent0202 {
    public static void main(String[] args) {
        String inventory = "";
        String[] lines = inventory.split("\n");
        List<Line> mappedLines = new ArrayList<>();
        for (String line : lines) {
            mappedLines.add(mapTheLine(line));
        }

        List<Detail> maxDetailList = mappedLines.stream()
                .map(line -> {
                    Detail maxDetail = new Detail(0,0,0);
                            for (Detail detail : line.details) {
                                if (detail.getRed() > maxDetail.getRed()){
                                    maxDetail.setRed(detail.getRed());
                                }
                                if (detail.getGreen() > maxDetail.getGreen()){
                                    maxDetail.setGreen(detail.getGreen());
                                }
                                if (detail.getBlue() > maxDetail.getBlue()){
                                    maxDetail.setBlue(detail.getBlue());
                                }
                            }
                            return maxDetail;
                        }
                )
                .toList();

        List<Long> powerList = maxDetailList.stream()
                .map(detail -> (long) detail.getRed() * (long) detail.getGreen() * (long) detail.getBlue())
                .toList();

        Long sum = powerList.stream()
                .reduce(0L, Long::sum);

        System.out.println(powerList);
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
            Detail detail = new Detail(0,0,0);
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

        public Detail(int red, int green, int blue) {
            this.red = red;
            this.green = green;
            this.blue = blue;
        }

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