package ca.jrvs.apps.practice;

import java.util.stream.IntStream;
import java.util.stream.Stream;

class LambdaStreamExcTesting {
  public static void main(String[] args) {
    LambdaStreamExcImpl streamImpl = new LambdaStreamExcImpl();


    String[] myStrings = {"yes", "no", "maybe" };
    int[] myInts = {1,2,5};
    Stream<String> myStringStream = streamImpl.toUpperCase(myStrings);
    myStringStream.forEach(name -> System.out.println(name));

    IntStream myIntStream = streamImpl.createIntStream(myInts);
    myIntStream = streamImpl.getOdd(myIntStream);
    myIntStream.forEach(name -> System.out.println(name));


   streamImpl.printMessages(myStrings, streamImpl.getLambdaPrinter("msg:", "!") );

   streamImpl.printOdd(streamImpl.createIntStream(0, 5), streamImpl.getLambdaPrinter("odd number:", "!"));
  }
}