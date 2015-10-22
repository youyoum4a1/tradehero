package com.tradehero.chinabuild.fragment.stockRecommend;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.LinearLayout;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tradehero.chinabuild.data.ManageEssentialDTO;
import com.tradehero.chinabuild.data.ManageLearningDTO;
import com.tradehero.chinabuild.data.ManageProductionDTO;
import com.tradehero.chinabuild.data.ManageTopDTO;
import com.tradehero.chinabuild.dialog.DialogFactory;
import com.tradehero.chinabuild.dialog.TimeLineDetailDialogLayout;
import com.tradehero.chinabuild.dialog.TimeLineReportDialogLayout;
import com.tradehero.chinabuild.fragment.message.TimeLineItemDetailFragment;
import com.tradehero.chinabuild.fragment.portfolio.PortfolioFragment;
import com.tradehero.chinabuild.fragment.userCenter.UserMainPage;
import com.tradehero.chinabuild.utils.UniversalImageLoader;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.activities.DisplayLargeImageActivity;
import com.tradehero.th.adapters.StockRecommendListAdapter.ViewHolder;
import com.tradehero.th.adapters.UserTimeLineAdapter;
import com.tradehero.th.api.share.wechat.WeChatDTO;
import com.tradehero.th.api.share.wechat.WeChatMessageType;
import com.tradehero.th.api.timeline.TimelineItemDTO;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.models.number.THSignedPercentage;
import com.tradehero.th.network.share.SocialSharerImpl;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * @author <a href="mailto:sam@tradehero.mobi"> Sam Yu </a>
 */
public class StockRecommendDetailFragment extends TimeLineItemDetailFragment {

    protected ViewHolder viewHolder;
    private TimelineItemDTO mTimelineItemDTO;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        setHeadViewMiddleMain(R.string.stock_recommend);
    }

    @Override
    public LinearLayout getHeaderView(LayoutInflater inflater) {
        return (LinearLayout) inflater.inflate(R.layout.fragment_stock_recommend_content, null);
    }

    @Override
    public void initRoot(View view) {
        viewHolder = new ViewHolder(view);
        llDisscurssOrNews = viewHolder.llItemAll;
        imgSecurityTLUserHeader = viewHolder.userIcon;
        tvUserTLTimeStamp = viewHolder.createTime;
        tvUserTLContent = viewHolder.articleContent;
        tvUserTLName = viewHolder.userName;
        btnTLPraise = viewHolder.btnTLPraise;
    }

    @Override
    public void displayDiscussOrNewsDTO() {
        mTimelineItemDTO = (TimelineItemDTO) getAbstractDiscussionCompactDTO();
        llDisscurssOrNews.setVisibility(mTimelineItemDTO == null ? View.INVISIBLE : View.VISIBLE);
        if (mTimelineItemDTO != null) {
            // User Image
            ImageLoader.getInstance().displayImage(mTimelineItemDTO.user.picture, viewHolder.userIcon, UniversalImageLoader.getAvatarImageLoaderOptions());
            // User name
            viewHolder.userName.setText(mTimelineItemDTO.user.getDisplayName());
            // User signature
            if (mTimelineItemDTO.user.signature == null) {
                viewHolder.userSignature.setVisibility(View.GONE);
            } else {
                viewHolder.userSignature.setText(mTimelineItemDTO.user.signature);
            }

            // User ROI
            THSignedPercentage roi = THSignedPercentage.builder(mTimelineItemDTO.user.roiSinceInception * 100).build();
            viewHolder.roi.setText(roi.toString());
            viewHolder.roi.setTextColor(getActivity().getResources().getColor(roi.getColorResId()));

            // Article
            viewHolder.articleTitle.setText(mTimelineItemDTO.header);
            viewHolder.articleContent.setText(mTimelineItemDTO.text);

            // Attachment image
            if (mTimelineItemDTO.picUrl == null) {
                viewHolder.attachmentImage.setVisibility(View.GONE);
            } else {
                ImageLoader.getInstance().displayImage(mTimelineItemDTO.picUrl, viewHolder.attachmentImage, UniversalImageLoader.getDisplayLargeImageOptions());
            }

            // Time
            viewHolder.createTime.setText(prettyTime.get().formatUnrounded(mTimelineItemDTO.createdAtUtc));

            // View count
            viewHolder.numberRead.setText(String.valueOf(mTimelineItemDTO.viewCount));
            viewHolder.numberPraised.setText(String.valueOf(mTimelineItemDTO.upvoteCount));
            viewHolder.numberComment.setText(String.valueOf(mTimelineItemDTO.commentCount));
            viewHolder.btnTLPraise.setBackgroundResource(mTimelineItemDTO.voteDirection == 1 ? R.drawable.like_selected : R.drawable.like);

            // Listeners
            viewHolder.userClickableArea.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putInt(UserMainPage.BUNDLE_USER_BASE_KEY, mTimelineItemDTO.user.id);
                    bundle.putBoolean(UserMainPage.BUNDLE_NEED_SHOW_PROFILE, false);
                    pushFragment(UserMainPage.class, bundle);
                }
            });
            viewHolder.userPositionClickableArea.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putInt(PortfolioFragment.BUNLDE_SHOW_PROFILE_USER_ID, mTimelineItemDTO.user.id);
                    pushFragment(PortfolioFragment.class, bundle);
                }
            });
            viewHolder.buttonPraised.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickedPraise();
                }
            });
            viewHolder.buttonComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    comments(getAbstractDiscussionCompactDTO());
                }
            });
            viewHolder.attachmentImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), DisplayLargeImageActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(DisplayLargeImageActivity.KEY_LARGE_IMAGE_URL, mTimelineItemDTO.picUrl);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    protected void setTimelineOperaterLLVisibility(int visible) {
        super.setTimelineOperaterLLVisibility(View.GONE);
    }

    @Override
    public void onClickHeadRight0() {
        if (getActivity() == null) {
            return;
        }

        if (dialogFactory == null) {
            dialogFactory = new DialogFactory();
        }

        boolean isDeleteAllowed = isDeleteAllowed(dataDto);

        boolean isReportAllowed = isReportAllowed(dataDto);
        timeLineDetailMenuDialog =
                dialogFactory.createTimeLineDetailDialog(getActivity(), new TimeLineDetailDialogLayout.TimeLineDetailMenuClickListener() {
                    @Override
                    public void onReportClick() {
                        timeLineDetailMenuDialog.dismiss();
                        if (getActivity() == null) {
                            return;
                        }
                        timeLineReportMenuDialog = dialogFactory.createTimeLineReportDialog(getActivity(),
                                new TimeLineReportDialogLayout.TimeLineReportMenuClickListener() {
                                    @Override
                                    public void onItemClickListener(int position) {
                                        timeLineReportMenuDialog.dismiss();
                                        sendReport(dataDto, position);
                                    }
                                });
                    }

                    @Override
                    public void onDeleteClick() {
                        timeLineDetailMenuDialog.dismiss();
                        if (dataDto != null) {
                            showDeleteTimeLineConfirmDlg(dataDto.id, DIALOG_TYPE_DELETE_TIMELINE);
                        }
                    }

                    @Override
                    public void onShareToWechatClick() {
                        shareToWechat();
                        timeLineDetailMenuDialog.dismiss();
                    }

                    @Override
                    public void onShareToMomentClick() {
                        shareToWechatMoment();
                        timeLineDetailMenuDialog.dismiss();
                    }

                    @Override
                    public void onFavoriteClick() {
                        if (dataDto == null || currentUserId == null) {
                            return;
                        }

                        if (dataDto instanceof TimelineItemDTO) {
                            int timeLineId = ((TimelineItemDTO) dataDto).id;
                            ManageEssentialDTO dto = new ManageEssentialDTO();
                            if (isFavorite()) {
                                dto.isEssential = false;
                            } else {
                                dto.isEssential = true;
                            }
                            administratorManageTimelineServiceWrapper.get().operationEssential(currentUserId.toUserBaseKey().key, timeLineId, dto, new ManagerOperateCallback());
                        }
                        timeLineDetailMenuDialog.dismiss();
                    }

                    @Override
                    public void onProductionClick() {
                        if (dataDto == null || currentUserId == null) {
                            return;
                        }
                        if (dataDto instanceof TimelineItemDTO) {
                            ManageProductionDTO dto = new ManageProductionDTO();
                            int timeLineId = ((TimelineItemDTO) dataDto).id;
                            if (isProduction()) {
                                dto.isNotice = false;
                            } else {
                                dto.isNotice = true;
                            }
                            administratorManageTimelineServiceWrapper.get().operationProduction(currentUserId.toUserBaseKey().key, timeLineId, dto, new ManagerOperateCallback());
                        }
                        timeLineDetailMenuDialog.dismiss();
                    }

                    @Override
                    public void onTopClick() {
                        if (dataDto == null || currentUserId == null) {
                            return;
                        }
                        if(timelineFrom.equals("")){
                            THToast.show("Please not...");
                            return;
                        }
                        if (dataDto instanceof TimelineItemDTO) {
                            int timeLineId = ((TimelineItemDTO) dataDto).id;
                            int originalStickType = ((TimelineItemDTO) dataDto).stickType;
                            ManageTopDTO dto = new ManageTopDTO();
                            if (isTop()) {
                                dto.stickType = UserTimeLineAdapter.toZero(originalStickType, timelineFrom);
                            }else{
                                dto.stickType = UserTimeLineAdapter.toOne(originalStickType, timelineFrom);
                            }
                            administratorManageTimelineServiceWrapper.get().operationTop(currentUserId.toUserBaseKey().key, timeLineId, dto, new ManagerOperateCallback());
                        }
                        timeLineDetailMenuDialog.dismiss();
                    }

                    @Override
                    public void onLearningClick() {
                        if (dataDto == null || currentUserId == null) {
                            return;
                        }
                        if (dataDto instanceof TimelineItemDTO) {
                            int timeLineId = ((TimelineItemDTO) dataDto).id;
                            ManageLearningDTO dto =new ManageLearningDTO();
                            if (isLearning()) {
                                dto.isGuide = false;
                            }else{
                                dto.isGuide = true;
                            }
                            administratorManageTimelineServiceWrapper.get().operationLearning(currentUserId.toUserBaseKey().key, timeLineId, dto, new ManagerOperateCallback());
                        }
                        timeLineDetailMenuDialog.dismiss();
                    }

                    @Override
                    public void onDeleteTimeLineClick() {
                        timeLineDetailMenuDialog.dismiss();
                        if (dataDto != null) {
                            showDeleteTimeLineConfirmDlg(dataDto.id, DIALOG_TYPE_DELETE_TIMELINE);
                        }
                    }
                }, isDeleteAllowed, isReportAllowed, isManager(), isTop(), isProduction(), isFavorite(), isLearning());
    }

    protected class ManagerOperateCallback implements Callback {

        @Override
        public void success(Object o, Response response) {
            THToast.show(R.string.administrator_operate_success);
            popCurrentFragment();
        }

        @Override
        public void failure(RetrofitError retrofitError) {
            THException exception = new THException(retrofitError);
            THToast.show(exception.toString());
        }
    }

    private void shareToWechat() {
        WeChatDTO weChatDTO = new WeChatDTO();
        weChatDTO.id = mTimelineItemDTO.id;
        weChatDTO.type = WeChatMessageType.StockRecommendToWeChat;
        weChatDTO.title = mTimelineItemDTO.user.getDisplayName();
        weChatDTO.description = mTimelineItemDTO.header + "," + mTimelineItemDTO.text;
        ((SocialSharerImpl) socialSharerLazy.get()).share(weChatDTO, getActivity());

    }
    private void shareToWechatMoment() {
        WeChatDTO weChatDTO = new WeChatDTO();
        weChatDTO.id = mTimelineItemDTO.id;
        weChatDTO.type = WeChatMessageType.StockRecommendToMoment;
        weChatDTO.title = mTimelineItemDTO.user.getDisplayName();
        weChatDTO.description = mTimelineItemDTO.header + "," + mTimelineItemDTO.text;
        ((SocialSharerImpl) socialSharerLazy.get()).share(weChatDTO, getActivity());
    }
}
