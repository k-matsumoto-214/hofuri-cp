package com.keisuke.hofuri.service;

import com.keisuke.hofuri.entity.CpInfo;
import com.keisuke.hofuri.exception.RegistrationFailureException;
import com.keisuke.hofuri.repository.CpInfosDao;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CpService {
  @Autowired
  CpInfosDao cpInfosDao;

  /**
   * メソッド呼び出し時のCP残高を保振から取得します。
   * @return オブジェクトCpInfoの配列を返します
   * @throws ParseException
   */
  public List<CpInfo> fetchTodaysCpBalance() throws InterruptedException, ParseException {
    // 結果返却用のリストを定義
    List<CpInfo> result = new ArrayList<>();
    // カンマ区切りの数字文字列を変換するためのフォーマッターを定義
    NumberFormat numberFormater = NumberFormat.getInstance();
    // 日付変換用のフォーマッターを定義
    SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy/MM/dd");

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
        (ExpectedCondition<Boolean>) webDriver -> webDriver.getTitle().startsWith("銘柄公示情報 （短期社債等）検索"));

    Thread.sleep(1000);

    //CP情報の取得を開始
    for (int repeatNumber = 0; repeatNumber < 50; repeatNumber++) {
      //発行体名の取得
      String name = driver.findElement(
                              By.cssSelector(
                                  "#MAIN > div:nth-child(3) > table > tbody > tr > td > font > table:nth-child(" + (repeatNumber + 8) +
                                  ") > tbody > tr > td > table > tbody > tr:nth-child(1) > td > span"))
                        .getText();
      // ISINCodeの取得
      String isinCode = driver.findElement(
                                  By.cssSelector(
                                      "#MAIN > div:nth-child(3) > table > tbody > tr > td > font > table:nth-child(" + (repeatNumber + 8) +
                                      ") > tbody > tr > td > table > tbody > tr:nth-child(7) > td:nth-child(2) > span"))
                            .getText();
      // 各社債の金額取得
      int bondUnit = numberFormater.parse(
                                       driver.findElement(
                                                 By.cssSelector(
                                                     "#MAIN > div:nth-child(3) > table > tbody > tr > td > font > table:nth-child(" + (repeatNumber + 8) +
                                                     ") > tbody > tr > td > table > tbody > tr:nth-child(7) > td:nth-child(4) > span"))
                                           .getText())
                         .intValue();
      //発行総額の取得
      int amount = numberFormater.parse(
                                     driver.findElement(
                                               By.cssSelector(
                                                   "#MAIN > div:nth-child(3) > table > tbody > tr > td > font > table:nth-child(" + (repeatNumber + 8) +
                                                   ") > tbody > tr > td > table > tbody > tr:nth-child(8) > td:nth-child(2) > span"))
                                         .getText())
                       .intValue();
      //発行者コードの取得
      String issureCode = isinCode.substring(5, 8);

      //残高更新日の取得
      Date fetchedDate = dateFormater.parse(
          driver.findElement(
                    By.cssSelector(
                        "#MAIN > div:nth-child(3) > table > tbody > tr > td > font > table:nth-child(5) > tbody > tr > td"))
              .getText()
              .substring(1, 11));

      //CP情報のインスタンスを生成
      CpInfo tempCpInfo = new CpInfo(null, name, isinCode, bondUnit, amount, issureCode, fetchedDate);

      result.add(tempCpInfo);
    }
    return result;
  }

  /**
   * リストで渡した残高情報をDBに登録します。
   * @param cpInfos CpInfo(残高情報)のリスト
   * @throws RegistrationFailureException DB登録に失敗した場合例外を投げます。
   */
  public void registerBalances(List<CpInfo> cpInfos) throws RegistrationFailureException {
    for (CpInfo cpInfo : cpInfos) {
      if (cpInfosDao.create(cpInfo) != 1) {
        throw new RegistrationFailureException("CP残高の登録に失敗しました。"
                                               + " : " + cpInfo.getIsinCode());
      }
    }
  }
}
