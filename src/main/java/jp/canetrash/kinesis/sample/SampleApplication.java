package jp.canetrash.kinesis.sample;

import java.util.Date;

import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.kinesisvideo.AmazonKinesisVideo;
import com.amazonaws.services.kinesisvideo.AmazonKinesisVideoArchivedMedia;
import com.amazonaws.services.kinesisvideo.AmazonKinesisVideoArchivedMediaClientBuilder;
import com.amazonaws.services.kinesisvideo.AmazonKinesisVideoClientBuilder;
import com.amazonaws.services.kinesisvideo.model.APIName;
import com.amazonaws.services.kinesisvideo.model.FragmentSelector;
import com.amazonaws.services.kinesisvideo.model.FragmentSelectorType;
import com.amazonaws.services.kinesisvideo.model.GetDataEndpointRequest;
import com.amazonaws.services.kinesisvideo.model.ListFragmentsRequest;
import com.amazonaws.services.kinesisvideo.model.ListFragmentsResult;
import com.amazonaws.services.kinesisvideo.model.TimestampRange;

public class SampleApplication {

	public static void main(String[] args) {

		AmazonKinesisVideoClientBuilder builder = AmazonKinesisVideoClientBuilder.standard();
		builder.setRegion("ap-northeast-1");
		AmazonKinesisVideo amazonKinesisVideo = builder.build();
		String endpoin = amazonKinesisVideo
				.getDataEndpoint(
						new GetDataEndpointRequest().withAPIName(APIName.LIST_FRAGMENTS).withStreamName("test"))
				.getDataEndpoint();

		AmazonKinesisVideoArchivedMediaClientBuilder archivedMediaClientBuilder = AmazonKinesisVideoArchivedMediaClientBuilder
				.standard();
		EndpointConfiguration endpointConfiguration = new EndpointConfiguration(endpoin, "ap-northeast-1");
		archivedMediaClientBuilder.setEndpointConfiguration(endpointConfiguration);
		AmazonKinesisVideoArchivedMedia media = archivedMediaClientBuilder.build();
		ListFragmentsRequest req = new ListFragmentsRequest();
		FragmentSelector fragmentSelector = new FragmentSelector();
		fragmentSelector.withFragmentSelectorType(FragmentSelectorType.PRODUCER_TIMESTAMP);
		TimestampRange timestampRange = new TimestampRange();
		timestampRange.setEndTimestamp(new Date());
		timestampRange.setStartTimestamp(new Date());
		fragmentSelector.setTimestampRange(timestampRange);
		req.setFragmentSelector(fragmentSelector);
		req.setStreamName("test");
		req.setMaxResults(100L);
		ListFragmentsResult res = media.listFragments(req);

		System.out.println(res.getFragments());
	}
}
