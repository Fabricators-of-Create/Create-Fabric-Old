package com.simibubi.create.content.logistics.item.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import com.simibubi.create.content.logistics.item.filter.AttributeFilterContainer.WhitelistMode;
import com.simibubi.create.content.logistics.item.filter.FilterScreenPacket.Option;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widgets.IconButton;
import com.simibubi.create.foundation.gui.widgets.Indicator;
import com.simibubi.create.foundation.gui.widgets.Label;
import com.simibubi.create.foundation.gui.widgets.SelectionScrollInput;
import com.simibubi.create.foundation.networking.AllPackets;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.foundation.utility.Pair;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class AttributeFilterScreen extends AbstractFilterScreen<AttributeFilterContainer> {

	private static final String PREFIX = "gui.attribute_filter.";

	private IconButton whitelistDis, whitelistCon, blacklist;
	private Indicator whitelistDisIndicator, whitelistConIndicator, blacklistIndicator;
	private IconButton add;
	private IconButton addInverted;

	private Text addDESC = Lang.translate(PREFIX + "add_attribute");
	private Text addInvertedDESC = Lang.translate(PREFIX + "add_inverted_attribute");

	private Text allowDisN = Lang.translate(PREFIX + "allow_list_disjunctive");
	private Text allowDisDESC = Lang.translate(PREFIX + "allow_list_disjunctive.description");
	private Text allowConN = Lang.translate(PREFIX + "allow_list_conjunctive");
	private Text allowConDESC = Lang.translate(PREFIX + "allow_list_conjunctive.description");
	private Text denyN = Lang.translate(PREFIX + "deny_list");
	private Text denyDESC = Lang.translate(PREFIX + "deny_list.description");

	private Text referenceH = Lang.translate(PREFIX + "add_reference_item");
	private Text noSelectedT = Lang.translate(PREFIX + "no_selected_attributes");
	private Text selectedT = Lang.translate(PREFIX + "selected_attributes");

	private ItemStack lastItemScanned = ItemStack.EMPTY;
	private List<ItemAttribute> attributesOfItem = new ArrayList<>();
	private List<Text> selectedAttributes = new ArrayList<>();
	private SelectionScrollInput attributeSelector;
	private Label attributeSelectorLabel;

	public AttributeFilterScreen(AttributeFilterContainer container, PlayerInventory inv, Text title) {
		super(container, inv, title, AllGuiTextures.ATTRIBUTE_FILTER);
	}

	@Override
	protected void init() {
		super.init();
		int x = x;
		int y = y;

		whitelistDis = new IconButton(x + 47, y + 59, AllIcons.I_WHITELIST_OR);
		whitelistDis.setToolTip(allowDisN);
		whitelistCon = new IconButton(x + 65, y + 59, AllIcons.I_WHITELIST_AND);
		whitelistCon.setToolTip(allowConN);
		blacklist = new IconButton(x + 83, y + 59, AllIcons.I_WHITELIST_NOT);
		blacklist.setToolTip(denyN);

		whitelistDisIndicator = new Indicator(x + 47, y + 53, LiteralText.EMPTY);
		whitelistConIndicator = new Indicator(x + 65, y + 53, LiteralText.EMPTY);
		blacklistIndicator = new Indicator(x + 83, y + 53, LiteralText.EMPTY);

		widgets.addAll(Arrays.asList(blacklist, whitelistCon, whitelistDis, blacklistIndicator, whitelistConIndicator,
			whitelistDisIndicator));

		widgets.add(add = new IconButton(x + 182, y + 21, AllIcons.I_ADD));
		widgets.add(addInverted = new IconButton(x + 200, y + 21, AllIcons.I_ADD_INVERTED_ATTRIBUTE));
		add.setToolTip(addDESC);
		addInverted.setToolTip(addInvertedDESC);

		handleIndicators();

		attributeSelectorLabel = new Label(x + 43, y + 26, LiteralText.EMPTY).colored(0xF3EBDE)
			.withShadow();
		attributeSelector = new SelectionScrollInput(x + 39, y + 21, 137, 18);
		attributeSelector.forOptions(Arrays.asList(LiteralText.EMPTY));
		attributeSelector.removeCallback();
		referenceItemChanged(handler.filterInventory.getStackInSlot(0));

		widgets.add(attributeSelector);
		widgets.add(attributeSelectorLabel);

		selectedAttributes.clear();
		selectedAttributes.add((handler.selectedAttributes.isEmpty() ? noSelectedT : selectedT).copy()
			.formatted(Formatting.YELLOW));
		handler.selectedAttributes.forEach(at -> selectedAttributes.add(new LiteralText("- ")
			.append(at.getFirst()
				.format(at.getSecond()))
			.formatted(Formatting.GRAY)));

	}

	private void referenceItemChanged(ItemStack stack) {
		lastItemScanned = stack;

		if (stack.isEmpty()) {
			attributeSelector.active = false;
			attributeSelector.visible = false;
			attributeSelectorLabel.text = referenceH.copy()
				.formatted(Formatting.ITALIC);
			add.active = false;
			addInverted.active = false;
			attributeSelector.calling(s -> {
			});
			return;
		}

		add.active = true;

		addInverted.active = true;
		attributeSelector.titled(stack.getName()
			.copy()
			.append("..."));
		attributesOfItem.clear();
		for (ItemAttribute itemAttribute : ItemAttribute.types)
			attributesOfItem.addAll(itemAttribute.listAttributesOf(stack, client.world));
		List<Text> options = attributesOfItem.stream()
			.map(a -> a.format(false))
			.collect(Collectors.toList());
		attributeSelector.forOptions(options);
		attributeSelector.active = true;
		attributeSelector.visible = true;
		attributeSelector.setState(0);
		attributeSelector.calling(i -> {
			attributeSelectorLabel.setTextAndTrim(options.get(i), true, 112);
			ItemAttribute selected = attributesOfItem.get(i);
			for (Pair<ItemAttribute, Boolean> existing : handler.selectedAttributes) {
				CompoundTag testTag = new CompoundTag();
				CompoundTag testTag2 = new CompoundTag();
				existing.getFirst()
					.serializeNBT(testTag);
				selected.serializeNBT(testTag2);
				if (testTag.equals(testTag2)) {
					add.active = false;
					addInverted.active = false;
					return;
				}
			}
			add.active = true;
			addInverted.active = true;
		});
		attributeSelector.onChanged();
	}

	@Override
	public void renderWindowForeground(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		ItemStack stack = handler.filterInventory.getStackInSlot(1);
		matrixStack.push();
		matrixStack.translate(0.0F, 0.0F, 32.0F);
		this.setZOffset(200);
		this.itemRenderer.zOffset = 200.0F;
		this.itemRenderer.renderGuiItemOverlay(textRenderer, stack, x + 22, y + 57,
			String.valueOf(selectedAttributes.size() - 1));
		this.setZOffset(0);
		this.itemRenderer.zOffset = 0.0F;
		matrixStack.pop();

		super.renderWindowForeground(matrixStack, mouseX, mouseY, partialTicks);
	}

	@Override
	public void tick() {
		super.tick();
		ItemStack stackInSlot = handler.filterInventory.getStackInSlot(0);
		if (!stackInSlot.equals(lastItemScanned, false))
			referenceItemChanged(stackInSlot);
	}

	@Override
	protected void drawMouseoverTooltip(MatrixStack matrixStack, int mouseX, int mouseY) {
		if (this.client.player.inventory.getCursorStack()
			.isEmpty() && this.focusedSlot != null && this.focusedSlot.hasStack()) {
			if (this.focusedSlot.id == 37) {
				renderTooltip(matrixStack, selectedAttributes, mouseX, mouseY);
				return;
			}
			this.renderTooltip(matrixStack, this.focusedSlot.getStack(), mouseX, mouseY);
		}
		super.drawMouseoverTooltip(matrixStack, mouseX, mouseY);
	}

	@Override
	protected List<IconButton> getTooltipButtons() {
		return Arrays.asList(blacklist, whitelistCon, whitelistDis);
	}

	@Override
	protected List<MutableText> getTooltipDescriptions() {
		return Arrays.asList(denyDESC.copy(), allowConDESC.copy(), allowDisDESC.copy());
	}

	@Override
	public boolean mouseClicked(double x, double y, int button) {
		boolean mouseClicked = super.mouseClicked(x, y, button);

		if (button != 0)
			return mouseClicked;

		if (blacklist.isHovered()) {
			handler.whitelistMode = WhitelistMode.BLACKLIST;
			sendOptionUpdate(Option.BLACKLIST);
			return true;
		}

		if (whitelistCon.isHovered()) {
			handler.whitelistMode = WhitelistMode.WHITELIST_CONJ;
			sendOptionUpdate(Option.WHITELIST2);
			return true;
		}

		if (whitelistDis.isHovered()) {
			handler.whitelistMode = WhitelistMode.WHITELIST_DISJ;
			sendOptionUpdate(Option.WHITELIST);
			return true;
		}

		if (add.isHovered() && add.active)
			return handleAddedAttibute(false);
		if (addInverted.isHovered() && addInverted.active)
			return handleAddedAttibute(true);

		return mouseClicked;
	}

	protected boolean handleAddedAttibute(boolean inverted) {
		int index = attributeSelector.getState();
		if (index >= attributesOfItem.size())
			return false;
		add.active = false;
		addInverted.active = false;
		CompoundTag tag = new CompoundTag();
		ItemAttribute itemAttribute = attributesOfItem.get(index);
		itemAttribute.serializeNBT(tag);
		AllPackets.channel
			.sendToServer(new FilterScreenPacket(inverted ? Option.ADD_INVERTED_TAG : Option.ADD_TAG, tag));
		handler.appendSelectedAttribute(itemAttribute, inverted);
		if (handler.selectedAttributes.size() == 1)
			selectedAttributes.set(0, selectedT.copy()
				.formatted(Formatting.YELLOW));
		selectedAttributes.add(new LiteralText("- ").append(itemAttribute.format(inverted))
			.formatted(Formatting.GRAY));
		return true;
	}

	@Override
	protected void contentsCleared() {
		selectedAttributes.clear();
		selectedAttributes.add(noSelectedT.copy()
			.formatted(Formatting.YELLOW));
		if (!lastItemScanned.isEmpty()) {
			add.active = true;
			addInverted.active = true;
		}
	}

	@Override
	protected boolean isButtonEnabled(IconButton button) {
		if (button == blacklist)
			return handler.whitelistMode != WhitelistMode.BLACKLIST;
		if (button == whitelistCon)
			return handler.whitelistMode != WhitelistMode.WHITELIST_CONJ;
		if (button == whitelistDis)
			return handler.whitelistMode != WhitelistMode.WHITELIST_DISJ;
		return true;
	}

	@Override
	protected boolean isIndicatorOn(Indicator indicator) {
		if (indicator == blacklistIndicator)
			return handler.whitelistMode == WhitelistMode.BLACKLIST;
		if (indicator == whitelistConIndicator)
			return handler.whitelistMode == WhitelistMode.WHITELIST_CONJ;
		if (indicator == whitelistDisIndicator)
			return handler.whitelistMode == WhitelistMode.WHITELIST_DISJ;
		return false;
	}

}
