package container;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.AfterClass;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class TestRunner {
  public static final String ANSI_RED = "\u001B[31m";
  public static final String ANSI_GREEN = "\u001B[32m";
  public static final String ANSI_RESET = "\u001B[0m";

  private static final String OK = " --- " + ANSI_GREEN + "ok" + ANSI_RESET;
  private static final String NG = " --- " + ANSI_RED + "ng" + ANSI_RESET;
  public static void main(String[] args) {
    final Class[] tests = {
        Test1.class
    };
    StringBuilder result = new StringBuilder();
    int successCount = 0;
    for (Class c:tests) {
      String className = c.getCanonicalName();
      try {
        check(c);
        result.append(className + OK);
        successCount++;
      } catch (Exception e) {
        e.printStackTrace();
        result.append(className + NG);
      }
      result.append("\n");
    }

    System.out.println(result.toString());
    System.out.println(successCount + " / " + tests.length + " tests succeed.");
  }

  private static void check(Class target) throws InvocationTargetException, IllegalAccessException, InstantiationException {
    Method beforeClass = null;
    Method afterClass = null;
    Method before = null;
    Method after = null;
    List<Method> tests = new ArrayList<>();

    System.out.println(target.getCanonicalName());

    Method[] methods = target.getMethods();
    for (int i = 0; i < methods.length; i++) {
      Method m = methods[i];
      Test testAnnotation = m.getAnnotation(Test.class);
      if (testAnnotation != null) {
        tests.add(m);
      }
      Before beforeAnnotation = m.getAnnotation(Before.class);
      if (beforeAnnotation != null) {
        before = m;
      }
      After afterAnnotation = m.getAnnotation(After.class);
      if (afterAnnotation != null) {
        after = m;
      }
      BeforeClass beforeClassAnnotation = m.getAnnotation(BeforeClass.class);
      if (beforeClassAnnotation != null) {
        beforeClass = m;
      }
      AfterClass afterClassAnnotation = m.getAnnotation(AfterClass.class);
      if (afterClassAnnotation != null) {
        afterClass = m;
      }
//      System.out.println(methods[i]);
    }

    // Run
    if (beforeClass != null) {
      beforeClass.invoke(null);
    }

    for (Method method: tests) {
      Object testTarget = target.newInstance();
      if (before != null) {
        before.invoke(testTarget);
      }
      System.out.println(method.getName());
      method.invoke(testTarget);
      if (after != null) {
        after.invoke(testTarget);
      }
    }
    if (afterClass != null) {
      afterClass.invoke(null);
    }
  }
}