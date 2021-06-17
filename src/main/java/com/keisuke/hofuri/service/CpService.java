package com.keisuke.hofuri.service;

import com.keisuke.hofuri.entity.CpInfo;
import com.keisuke.hofuri.exception.AlreadyFetchedException;
import com.keisuke.hofuri.exception.RegistrationFailureException;
import com.keisuke.hofuri.repository.CpInfosDao;
import com.keisuke.hofuri.repository.WorkdaysDao;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
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
  @Autowired
  WorkdaysDao workdaysDao;

  /**
   * seleniumのchromeブラウザドライバを取得します。
   * @return 
   * @throws InterruptedException
   */
  public WebDriver getChoromDriver() {
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
    return new ChromeDriver();
  }

  /**
   * * メソッド呼び出し時のCP残高を保振から取得します。
   * @param driver webドライバー
   * @return オブジェクトCpInfoの配列を返します
   * @throws InterruptedException
   * @throws ParseException
   * @throws AlreadyFetchedException　取得対象の日付の残高を取得済みの場合
   */
  public List<CpInfo> fetchTodaysCpBalance(WebDriver driver) throws InterruptedException, ParseException, AlreadyFetchedException {
    // 結果返却用のリストを定義
    List<CpInfo> result = new ArrayList<>();
    // カンマ区切りの数字文字列を変換するためのフォーマッターを定義
    NumberFormat numberFormater = NumberFormat.getInstance();
    // 日付変換用のフォーマッターを定義
    SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy/MM/dd");

    //保振のページを開く
    driver.get("https://www.jasdec.com/reading/cpmei.php");

    Thread.sleep(1000);

    //検索ボタンをクリック
    driver
        .findElement(By.cssSelector(
            "#MAIN > div:nth-child(3) > table > tbody > tr > td > font > table:nth-child(8) > tbody > tr > td > input[type=submit]:nth-child(4)"))
        .click();

    // ページの更新を待つ 5秒でタイムアウト
    new WebDriverWait(driver, 5).until(
        (ExpectedCondition<Boolean>) webDriver -> webDriver.getTitle().startsWith("銘柄公示情報 （短期社債等）検索"));

    // 残高更新日の取得
    Date fetchedDate = dateFormater.parse(
        driver.findElement(
                  By.cssSelector(
                      "#MAIN > div:nth-child(3) > table > tbody > tr > td > font > table:nth-child(5) > tbody > tr > td"))
            .getText()
            .substring(1, 11));

    // 取得対象の日付の残高をすでに取得している場合例外を投げる
    if (cpInfosDao.isFetched(fetchedDate)) {
      driver.quit();
      throw new AlreadyFetchedException("同日の残高情報はすでに取得しています。");
    }

    /*
    CP情報の取得を開始する
    次へボタンがない or
    ページ移動に失敗する or 
    CP情報が50件に満たないページがある場合処理を終了する
    */
    try {
      //最初の10ページのセットのみ次へボタンの配置が異なるので分岐用のフラグ
      boolean isFirstSet = true;
      //　10ページ分CP情報取得を繰り返す
      for (int pageNumber = 1; pageNumber <= 10;) {
        //----------------------------ここからCP情報を50件取得する--------------------------------------------------------------------
        for (int repeatNumber = 0; repeatNumber < 50; repeatNumber++) {
          // 発行体名の取得
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
          // 発行総額の取得
          int amount = numberFormater.parse(
                                         driver.findElement(
                                                   By.cssSelector(
                                                       "#MAIN > div:nth-child(3) > table > tbody > tr > td > font > table:nth-child(" + (repeatNumber + 8) +
                                                       ") > tbody > tr > td > table > tbody > tr:nth-child(8) > td:nth-child(2) > span"))
                                             .getText())
                           .intValue();
          // 発行者コードの取得
          String issureCode = isinCode.substring(5, 8);

          // CP情報のインスタンスを生成し結果配列に格納
          CpInfo tempCpInfo = new CpInfo(null, name, isinCode, bondUnit, amount, issureCode, fetchedDate);
          result.add(tempCpInfo);
        }
        //----------------------------ここまで CP情報を50件取得する--------------------------------------------------------------------

        Thread.sleep(1000);

        if (pageNumber == 10) { // 現在10ページ目の場合次のセットへ移動する
          pageNumber = 1;       // 現在ページ数をリセット
          if (isFirstSet) {     //最初のセットの場合
            driver.findElement(
                      By.cssSelector(
                          "#MAIN > div:nth-child(3) > table > tbody > tr > td > font > table:nth-child(6) > tbody > tr > td > a:nth-child(11)"))
                .click();
            isFirstSet = false; //最初のセット判定フラグをOFF
          } else {              //最初のセットでない場合
            driver.findElement(
                      By.cssSelector(
                          "#MAIN > div:nth-child(3) > table > tbody > tr > td > font > table:nth-child(6) > tbody > tr > td > a:nth-child(12)"))
                .click();
          }
        } else {            // 現在10ページ目でない場合次のページへ移動する
          if (isFirstSet) { //最初のセットの場合
            driver.findElement(
                      By.cssSelector(
                          "#MAIN > div:nth-child(3) > table > tbody > tr > td > font > table:nth-child(6) > tbody > tr > td > a:nth-child(" + (pageNumber + 1) +
                          ")"))
                .click();
          } else { //最初のセットでない場合
            driver.findElement(
                      By.cssSelector(
                          "#MAIN > div:nth-child(3) > table > tbody > tr > td > font > table:nth-child(6) > tbody > tr > td > a:nth-child(" + (pageNumber + 2) +
                          ")"))
                .click();
          }
          pageNumber++; //ページナンバーをインクリメント
        }
        // ページの更新を待つ 5秒でタイムアウト
        new WebDriverWait(driver, 5).until(
            (ExpectedCondition<Boolean>) webDriver -> webDriver.getTitle().startsWith("銘柄公示情報 （短期社債等）検索"));
      }
    } catch (NoSuchElementException e) {
    } finally {
      driver.quit();
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

  public void fetchWorkdays() {
    List<Date> workdays = workdaysDao.fetchWorkdays();
    System.out.println(workdays);
  }
}
