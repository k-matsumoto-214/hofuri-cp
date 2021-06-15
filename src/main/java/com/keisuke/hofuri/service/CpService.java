package com.keisuke.hofuri.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

@Service
public class CpService {

  /**
   * メソッド呼び出し時のCP残高を保振から取得します。
   * @return オブジェクトCpInfoの配列を返します。
   */
  public List<Map<String, Object>> fetchTodaysCpBalance()
      throws InterruptedException {

    //結果返却用のリストを定義
    List<Map<String, Object>> result = new ArrayList<>();

    // ChromeDriverのパスを指定
    System.setProperty("webdriver.chrome.driver",
                       "chromedriver/chromedriver.exe");

    /*ヘッドレスの場合
    // headlessの設定
    ChromeOptions options = new ChromeOptions();
    options.addArguments("--headless");
    // chromedriverの取得
    WebDriver driver = new ChromeDriver(options);
    */

    // chromedriverの取得
    WebDriver driver = new ChromeDriver();
    //保振のページを開く
    driver.get("https://www.jasdec.com/reading/cpmei.php");
    System.out.println("Page title is: " + driver.getTitle());

    Thread.sleep(1000);

    //検索ボタンをクリック
    driver
        .findElement(By.cssSelector(
            "#MAIN > div:nth-child(3) > table > tbody > tr > td > font > table:nth-child(8) > tbody > tr > td > input[type=submit]:nth-child(4)"))
        .click();

    // ページの更新を待つ 5秒でタイムアウト
    new WebDriverWait(driver, 5).until(
        (ExpectedCondition<Boolean>)webDriver
        -> webDriver.getTitle().startsWith("銘柄公示情報 （短期社債等）検索"));

    //発行体名の取得
    WebElement nameElement = driver.findElement(By.cssSelector(
        "#MAIN > div:nth-child(3) > table > tbody > tr > td > font > table:nth-child(8) > tbody > tr > td > table > tbody > tr:nth-child(1) > td > span"));
    // ISINCodeの取得
    WebElement isinCodeElement = driver.findElement(By.cssSelector(
        "#MAIN > div:nth-child(3) > table > tbody > tr > td > font > table:nth-child(8) > tbody > tr > td > table > tbody > tr:nth-child(7) > td:nth-child(2) > span"));
    Map<String, Object> tempCpInfo = new LinkedHashMap<>();
    tempCpInfo.put("name", nameElement.getText());
    tempCpInfo.put("isinCode", isinCodeElement.getText());

    Thread.sleep(1000);

    result.add(tempCpInfo);
    return result;
  }
}
