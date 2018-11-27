package jp.canetrash.kinesis.sample;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.kinesisvideo.AmazonKinesisVideo;
import com.amazonaws.services.kinesisvideo.AmazonKinesisVideoArchivedMedia;
import com.amazonaws.services.kinesisvideo.AmazonKinesisVideoArchivedMediaClientBuilder;
import com.amazonaws.services.kinesisvideo.AmazonKinesisVideoClientBuilder;
import com.amazonaws.services.kinesisvideo.model.APIName;
import com.amazonaws.services.kinesisvideo.model.Fragment;
import com.amazonaws.services.kinesisvideo.model.FragmentSelector;
import com.amazonaws.services.kinesisvideo.model.FragmentSelectorType;
import com.amazonaws.services.kinesisvideo.model.GetDataEndpointRequest;
import com.amazonaws.services.kinesisvideo.model.GetMediaForFragmentListRequest;
import com.amazonaws.services.kinesisvideo.model.GetMediaForFragmentListResult;
import com.amazonaws.services.kinesisvideo.model.ListFragmentsRequest;
import com.amazonaws.services.kinesisvideo.model.ListFragmentsResult;
import com.amazonaws.services.kinesisvideo.model.TimestampRange;

public class SampleApplication {

	private static String streamName = "test";

	public static void main(String[] args) throws Exception {

		// エンドポイント取得
		AmazonKinesisVideoClientBuilder builder = AmazonKinesisVideoClientBuilder.standard();
		builder.setRegion("ap-northeast-1");
		AmazonKinesisVideo amazonKinesisVideo = builder.build();
		String endpoin = amazonKinesisVideo
				.getDataEndpoint(
						new GetDataEndpointRequest().withAPIName(APIName.LIST_FRAGMENTS).withStreamName(streamName))
				.getDataEndpoint();

		AmazonKinesisVideoArchivedMediaClientBuilder archivedMediaClientBuilder = AmazonKinesisVideoArchivedMediaClientBuilder
				.standard();
		EndpointConfiguration endpointConfiguration = new EndpointConfiguration(endpoin, "ap-northeast-1");
		archivedMediaClientBuilder.setEndpointConfiguration(endpointConfiguration);

		// アーカイブされたFragmentの情報取得
		AmazonKinesisVideoArchivedMedia media = archivedMediaClientBuilder.build();
		ListFragmentsRequest req = new ListFragmentsRequest();
		FragmentSelector fragmentSelector = new FragmentSelector();
		fragmentSelector.withFragmentSelectorType(FragmentSelectorType.SERVER_TIMESTAMP);
		TimestampRange timestampRange = new TimestampRange();
		timestampRange.setStartTimestamp(new Date(0));
		timestampRange.setEndTimestamp(new Date());
		fragmentSelector.setTimestampRange(timestampRange);
		req.setFragmentSelector(fragmentSelector);
		req.setStreamName(streamName);
		ListFragmentsResult res = media.listFragments(req);

		List<Fragment> fragmentsList = res.getFragments();
		System.out.println("nextToken:" + res.getNextToken());
		System.out.println("#############");
		System.out.println(fragmentsList);
		System.out.println("#############");

		List<String> fragments = new ArrayList<String>();
		for (Fragment fragment : fragmentsList) {
			System.out.println(fragment.toString());
			fragments.add(fragment.getFragmentNumber());
		}

		// Fragmentの書き出し
		AmazonKinesisVideoArchivedMediaClientBuilder mediaBuilder = AmazonKinesisVideoArchivedMediaClientBuilder
				.standard();
		mediaBuilder.setEndpointConfiguration(endpointConfiguration);
		AmazonKinesisVideoArchivedMedia archivedMedia = mediaBuilder.build();
		GetMediaForFragmentListRequest getMediaForFragmentListRequest = new GetMediaForFragmentListRequest();
		getMediaForFragmentListRequest.setFragments(fragments);
		getMediaForFragmentListRequest.setStreamName(streamName);
		GetMediaForFragmentListResult result = archivedMedia.getMediaForFragmentList(getMediaForFragmentListRequest);
		File file = new File("./", "output.mp4");
		Files.copy(result.getPayload(), file.toPath());
	}
}
