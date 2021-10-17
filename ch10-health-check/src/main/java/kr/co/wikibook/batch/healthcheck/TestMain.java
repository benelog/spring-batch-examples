package kr.co.wikibook.batch.healthcheck;

public class TestMain {

  public static void main(String[] args) {
    var obj1 = new TestObj(300);
    var obj2 = new TestObj(300);
    if (obj1.getMyValue() == obj2.getMyValue()) {
      System.out.println("same");
    }

    new Integer(300);
    Integer num1 = 300;

    if (obj1.getMyValue() == 300) {
      System.out.println("300 == num1");
    }

    if (obj1.getMyValue().equals(300)) {
      System.out.println("num1 == 300");
    }

  }

  static class TestObj {

    private final Integer myValue;

    public TestObj(Integer myValue) {
      this.myValue = myValue;
    }

    public Integer getMyValue() {
      return myValue;
    }
  }
}


