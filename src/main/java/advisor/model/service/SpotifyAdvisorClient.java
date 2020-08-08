package advisor.model.service;

import advisor.model.dto.CommandLinePrintable;
import advisor.model.dto.Page;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

public interface SpotifyAdvisorClient {

    @RequestLine("GET /v1/browse/{resourcePath}?limit={limit}&offset={offset}" +
            "&country={country}&locale={locale}")
    @Headers({
            "Accept: application/json",
            "Content-Type: application/json",
            "Authorization: Bearer {accessToken}"})
    <T extends CommandLinePrintable> Page<T> resourcePage(
            @Param("resourcePath") String resourcePath,
            @Param("accessToken") String accessToken,
            @Param("limit") int limit,
            @Param("offset") int offset,
            @Param("country") String country,
            @Param("locale") String locale);

    @RequestLine("GET /v1/browse/{resourcePath}?country={country}&locale={locale}")
    @Headers({
            "Accept: application/json",
            "Content-Type: application/json",
            "Authorization: Bearer {accessToken}"})
    <T extends CommandLinePrintable> Page<T> resourcePage(
            @Param("resourcePath") String resourcePath,
            @Param("accessToken") String accessToken,
            @Param("country") String country,
            @Param("locale") String locale);
}
