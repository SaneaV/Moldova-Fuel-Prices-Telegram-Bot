package md.fuel.api.rest.wrapper;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.stream.Stream;
import md.fuel.api.facade.FillingStationFacade;
import md.fuel.api.rest.dto.FillingStationDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class FillingStationPageWrapperTest {

  private static final FillingStationDto FILLING_STATION_FORTRESS = new FillingStationDto("Fortress", 25.75, 22.3, 13.45,
      46.34746746138542, 28.947447953963454);
  private static final FillingStationDto FILLING_STATION_PETROM = new FillingStationDto("Fortress", 25.75, 22.3, 13.45,
      46.34746746138542, 28.947447953963454);

  private static final double LATITUDE = 10;
  private static final double LONGITUDE = 20;
  private static final double RADIUS = 30;
  private static final int LIMIT = 1;
  private static final String FUEL_TYPE = "FUEL TYPE";

  private final FillingStationFacade fillingStationFacade;
  private final FillingStationPageWrapper fillingStationPageWrapper;

  public FillingStationPageWrapperTest() {
    this.fillingStationFacade = mock(FillingStationFacade.class);
    this.fillingStationPageWrapper = new FillingStationPageWrapper(fillingStationFacade);
  }

  @ParameterizedTest
  @MethodSource("getData")
  @DisplayName("Should wrap all filling stations into a page")
  void shouldWrapAllFillingStations(int pageLimit, int offset, int finalSize, List<FillingStationDto> finalList) {
    when(fillingStationFacade.getAllFillingStations(anyDouble(), anyDouble(), anyDouble(), anyInt())).thenReturn(
        List.of(FILLING_STATION_FORTRESS, FILLING_STATION_PETROM));

    final PageDto<FillingStationDto> result = fillingStationPageWrapper.wrapAllFillingStationsList(LATITUDE, LONGITUDE, RADIUS,
        LIMIT, pageLimit, offset);

    assertThat(result.getTotalResults()).isEqualTo(2);
    assertThat(result.getItems()).hasSize(finalSize);
    assertThat(result.getItems()).hasSameElementsAs(finalList);
  }

  @ParameterizedTest
  @MethodSource("getData")
  @DisplayName("Should wrap best fuel price stations into a page")
  void shouldWrapBestFuelPriceStations(int pageLimit, int offset, int finalSize, List<FillingStationDto> finalList) {
    when(fillingStationFacade.getBestFuelPrice(anyDouble(), anyDouble(), anyDouble(), anyString(), anyInt())).thenReturn(
        List.of(FILLING_STATION_FORTRESS, FILLING_STATION_PETROM));

    final PageDto<FillingStationDto> result = fillingStationPageWrapper.wrapBestFuelPriceStation(LATITUDE, LONGITUDE, RADIUS,
        FUEL_TYPE, LIMIT, pageLimit, offset);

    assertThat(result.getTotalResults()).isEqualTo(2);
    assertThat(result.getItems()).hasSize(finalSize);
    assertThat(result.getItems()).hasSameElementsAs(finalList);
  }

  private static Stream<Arguments> getData() {
    final List<FillingStationDto> allFuelStations = List.of(FILLING_STATION_FORTRESS, FILLING_STATION_PETROM);
    final List<FillingStationDto> fortressList = singletonList(FILLING_STATION_FORTRESS);
    final List<FillingStationDto> petromList = singletonList(FILLING_STATION_PETROM);

    return Stream.of(
        Arguments.of(0, 0, 0, emptyList()),
        Arguments.of(2, 0, 2, allFuelStations),
        Arguments.of(2, 1, 1, petromList),
        Arguments.of(1, 0, 1, fortressList),
        Arguments.of(2, 2, 0, emptyList())
    );
  }
}
