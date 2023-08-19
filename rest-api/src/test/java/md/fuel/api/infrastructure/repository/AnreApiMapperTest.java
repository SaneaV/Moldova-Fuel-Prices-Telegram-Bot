package md.fuel.api.infrastructure.repository;

import static org.assertj.core.api.Assertions.assertThat;

import md.fuel.api.domain.FillingStation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class AnreApiMapperTest {

  private static final String FILLING_STATION_NAME = "Filling Station";
  private static final double PETROL_PRICE = 10;
  private static final double DIESEL_PRICE = 20;
  private static final double GAS_PRICE = 30;
  private static final double LATITUDE = 10;
  private static final double LONGITUDE = 10;

  private final AnreApiMapper anreApiMapper;

  public AnreApiMapperTest() {
    this.anreApiMapper = new AnreApiMapperImpl();
  }

  @Test
  @DisplayName("Should map fillingStationApi to fuelStation")
  void shouldMapFillingStationApiToFillingStation() {
    final FillingStationApi fillingStationApi = new FillingStationApi(FILLING_STATION_NAME, PETROL_PRICE, DIESEL_PRICE, GAS_PRICE,
        LATITUDE, LONGITUDE);

    final FillingStation result = anreApiMapper.toEntity(fillingStationApi);

    assertThat(result.getName()).isEqualTo(fillingStationApi.getName());
    assertThat(result.getPetrol()).isEqualTo(fillingStationApi.getPetrol());
    assertThat(result.getDiesel()).isEqualTo(fillingStationApi.getDiesel());
    assertThat(result.getGas()).isEqualTo(fillingStationApi.getGas());
    assertThat(result.getLongitude()).isEqualTo(fillingStationApi.getCoordinates().fst());
    assertThat(result.getLatitude()).isEqualTo(fillingStationApi.getCoordinates().snd());
  }

  @Test
  @DisplayName("Should return null on null fillingStationApi")
  void shouldReturnNullOnNullFillingStationApi() {
    final FillingStationApi fillingStationApi = null;

    final FillingStation result = anreApiMapper.toEntity(fillingStationApi);

    assertThat(result).isNull();
  }

  @Test
  @DisplayName("Should map zero or null price to null")
  void shouldMapZeroOrNullPriceToNull() {
    final FillingStationApi fillingStationApi = new FillingStationApi(FILLING_STATION_NAME, 0.0, null, 0.0D, LATITUDE, LONGITUDE);

    final FillingStation result = anreApiMapper.toEntity(fillingStationApi);

    assertThat(result.getName()).isEqualTo(fillingStationApi.getName());
    assertThat(result.getPetrol()).isNull();
    assertThat(result.getDiesel()).isNull();
    assertThat(result.getGas()).isNull();
    assertThat(result.getLongitude()).isEqualTo(fillingStationApi.getCoordinates().fst());
    assertThat(result.getLatitude()).isEqualTo(fillingStationApi.getCoordinates().snd());
  }
}