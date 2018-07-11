package theakki.synctool;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import theakki.synctool.Job.IncludeExclude.AnalyzeHelper;
import theakki.synctool.Job.IncludeExclude.AnalyzeResult;
import theakki.synctool.Job.IncludeExclude.AnalyzeStrategy;
import theakki.synctool.Job.Settings.OneWayStrategy;
import theakki.synctool.Job.Settings.SettingsHelper;
import theakki.synctool.Job.Settings.SyncDirection;
import theakki.synctool.Job.Settings.TwoWayStrategy;

import static org.junit.Assert.assertEquals;


public class SettingsHelper_Test
{
    @Test
    public void SyncDirectionFromString_Booth() throws Exception
    {
        final SyncDirection dirUPPER = SettingsHelper.SyncDirectionFromString("BOOTH", false, SyncDirection.ToA);
        assertEquals(SyncDirection.Booth, dirUPPER);

        final SyncDirection dirLower = SettingsHelper.SyncDirectionFromString("booth", false, SyncDirection.ToA);
        assertEquals(SyncDirection.Booth, dirLower);

        final SyncDirection dirMixed1 = SettingsHelper.SyncDirectionFromString("bOOth", false, SyncDirection.ToA);
        assertEquals(SyncDirection.Booth, dirMixed1);
    }


    @Test
    public void SyncDirectionFromString_ToA() throws Exception
    {
        final SyncDirection dirUPPER = SettingsHelper.SyncDirectionFromString("TOA", false, SyncDirection.Booth);
        assertEquals(SyncDirection.ToA, dirUPPER);

        final SyncDirection dirLower = SettingsHelper.SyncDirectionFromString("toa", false, SyncDirection.Booth);
        assertEquals(SyncDirection.ToA, dirLower);

        final SyncDirection dirMixed1 = SettingsHelper.SyncDirectionFromString("tOa", false, SyncDirection.Booth);
        assertEquals(SyncDirection.ToA, dirMixed1);
    }


    @Test
    public void SyncDirectionFromString_ToB() throws Exception
    {
        final SyncDirection dirUPPER = SettingsHelper.SyncDirectionFromString("TOB", false, SyncDirection.Booth);
        assertEquals(SyncDirection.ToB, dirUPPER);

        final SyncDirection dirLower = SettingsHelper.SyncDirectionFromString("tob", false, SyncDirection.Booth);
        assertEquals(SyncDirection.ToB, dirLower);

        final SyncDirection dirMixed1 = SettingsHelper.SyncDirectionFromString("TOb", false, SyncDirection.Booth);
        assertEquals(SyncDirection.ToB, dirMixed1);
    }

    @Test
    public void SyncDirectionFromString_Default() throws Exception
    {
        final SyncDirection dirInv1 = SettingsHelper.SyncDirectionFromString("Invalid", false, SyncDirection.Booth);
        assertEquals(SyncDirection.Booth, dirInv1);

        final SyncDirection dirInv2 = SettingsHelper.SyncDirectionFromString("Invalid", false, SyncDirection.ToA);
        assertEquals(SyncDirection.ToA, dirInv2);

        final SyncDirection dirInv3 = SettingsHelper.SyncDirectionFromString("Invalid", false, SyncDirection.ToB);
        assertEquals(SyncDirection.ToB, dirInv3);
    }

    @Test(expected=IllegalArgumentException.class)
    public void SyncDirectionFromString_Exception() throws Exception
    {
        SettingsHelper.SyncDirectionFromString("Invalid", true, SyncDirection.Booth);
    }


    @Test
    public void TwoWayStrategyFromString_AWins() throws Exception
    {
        final TwoWayStrategy dirUPPER = SettingsHelper.TwoWayStrategyFromString("AWINS", false, TwoWayStrategy.BWins);
        assertEquals(TwoWayStrategy.AWins, dirUPPER);

        final TwoWayStrategy dirLower = SettingsHelper.TwoWayStrategyFromString("awins", false, TwoWayStrategy.BWins);
        assertEquals(TwoWayStrategy.AWins, dirLower);

        final TwoWayStrategy dirMixed1 = SettingsHelper.TwoWayStrategyFromString("aWins", false, TwoWayStrategy.BWins);
        assertEquals(TwoWayStrategy.AWins, dirMixed1);
    }


    @Test
    public void TwoWayStrategyFromString_BWins() throws Exception
    {
        final TwoWayStrategy dirUPPER = SettingsHelper.TwoWayStrategyFromString("BWINS", false, TwoWayStrategy.AWins);
        assertEquals(TwoWayStrategy.BWins, dirUPPER);

        final TwoWayStrategy dirLower = SettingsHelper.TwoWayStrategyFromString("bwins", false, TwoWayStrategy.AWins);
        assertEquals(TwoWayStrategy.BWins, dirLower);

        final TwoWayStrategy dirMixed1 = SettingsHelper.TwoWayStrategyFromString("bWIns", false, TwoWayStrategy.AWins);
        assertEquals(TwoWayStrategy.BWins, dirMixed1);
    }


    @Test
    public void TwoWayStrategyFromString_Default() throws Exception
    {
        final TwoWayStrategy stratINV1 = SettingsHelper.TwoWayStrategyFromString("Invalid", false, TwoWayStrategy.AWins);
        assertEquals(TwoWayStrategy.AWins, stratINV1);

        final TwoWayStrategy stratINV2 = SettingsHelper.TwoWayStrategyFromString("Invalid", false, TwoWayStrategy.BWins);
        assertEquals(TwoWayStrategy.BWins, stratINV2);
    }


    @Test(expected = IllegalArgumentException.class)
    public void TwoWayStrategyFromString_Exception() throws Exception
    {
        SettingsHelper.TwoWayStrategyFromString("Invalid", true, TwoWayStrategy.AWins);
    }


    @Test
    public void OneWayStrategyFromString_Standard() throws Exception
    {
        final OneWayStrategy strategyUpper = SettingsHelper.OneWayStrategyFromString("STANDARD", false, OneWayStrategy.Mirror);
        assertEquals(OneWayStrategy.Standard, strategyUpper);

        final OneWayStrategy strategyLower = SettingsHelper.OneWayStrategyFromString("standard", false, OneWayStrategy.Mirror);
        assertEquals(OneWayStrategy.Standard, strategyLower);

        final OneWayStrategy strategyMixed1 = SettingsHelper.OneWayStrategyFromString("StanDard", false, OneWayStrategy.Mirror);
        assertEquals(OneWayStrategy.Standard, strategyMixed1);
    }


    @Test
    public void OneWayStrategyFromString_Mirror() throws Exception
    {
        final OneWayStrategy strategyUpper = SettingsHelper.OneWayStrategyFromString("MIRROR", false, OneWayStrategy.AllFilesInDateFolder);
        assertEquals(OneWayStrategy.Mirror, strategyUpper);

        final OneWayStrategy strategyLower = SettingsHelper.OneWayStrategyFromString("mirror", false, OneWayStrategy.AllFilesInDateFolder);
        assertEquals(OneWayStrategy.Mirror, strategyLower);

        final OneWayStrategy strategyMixed1 = SettingsHelper.OneWayStrategyFromString("MirRoR", false, OneWayStrategy.AllFilesInDateFolder);
        assertEquals(OneWayStrategy.Mirror, strategyMixed1);
    }


    @Test
    public void OneWayStrategyFromString_AllFilesDate() throws Exception
    {
        final OneWayStrategy strategyUpper = SettingsHelper.OneWayStrategyFromString("ALLFILESINDATEFOLDER", false, OneWayStrategy.Mirror);
        assertEquals(OneWayStrategy.AllFilesInDateFolder, strategyUpper);

        final OneWayStrategy strategyLower = SettingsHelper.OneWayStrategyFromString("allfilesindatefolder", false, OneWayStrategy.Mirror);
        assertEquals(OneWayStrategy.AllFilesInDateFolder, strategyLower);

        final OneWayStrategy strategyMixed1 = SettingsHelper.OneWayStrategyFromString("aLLfILesiNdATEfoLDER", false, OneWayStrategy.Mirror);
        assertEquals(OneWayStrategy.AllFilesInDateFolder, strategyMixed1);
    }


    @Test
    public void OneWayStrategyFromString_NewFilesDate() throws Exception
    {
        final OneWayStrategy strategyUpper = SettingsHelper.OneWayStrategyFromString("NEWFILESINDATEFOLDER", false, OneWayStrategy.Mirror);
        assertEquals(OneWayStrategy.NewFilesInDateFolder, strategyUpper);

        final OneWayStrategy strategyLower = SettingsHelper.OneWayStrategyFromString("newfilesindatefolder", false, OneWayStrategy.Mirror);
        assertEquals(OneWayStrategy.NewFilesInDateFolder, strategyLower);

        final OneWayStrategy strategyMixed1 = SettingsHelper.OneWayStrategyFromString("nEwfILesiNdATEfoLDER", false, OneWayStrategy.Mirror);
        assertEquals(OneWayStrategy.NewFilesInDateFolder, strategyMixed1);
    }


    @Test
    public void OneWayStrategyFromString_Move() throws Exception
    {
        final OneWayStrategy strategyUpper = SettingsHelper.OneWayStrategyFromString("MOVEFILES", false, OneWayStrategy.Mirror);
        assertEquals(OneWayStrategy.MoveFiles, strategyUpper);

        final OneWayStrategy strategyLower = SettingsHelper.OneWayStrategyFromString("movefiles", false, OneWayStrategy.Mirror);
        assertEquals(OneWayStrategy.MoveFiles, strategyLower);

        final OneWayStrategy strategyMixed1 = SettingsHelper.OneWayStrategyFromString("moVefILes", false, OneWayStrategy.Mirror);
        assertEquals(OneWayStrategy.MoveFiles, strategyMixed1);
    }


    @Test
    public void OneWayStrategyFromString_MoveFilesDate() throws Exception
    {
        final OneWayStrategy strategyUpper = SettingsHelper.OneWayStrategyFromString("MOVEFILESINDATEFOLDER", false, OneWayStrategy.Mirror);
        assertEquals(OneWayStrategy.MoveFilesInDateFolder, strategyUpper);

        final OneWayStrategy strategyLower = SettingsHelper.OneWayStrategyFromString("movefilesindatefolder", false, OneWayStrategy.Mirror);
        assertEquals(OneWayStrategy.MoveFilesInDateFolder, strategyLower);

        final OneWayStrategy strategyMixed1 = SettingsHelper.OneWayStrategyFromString("moVEfilEsinDatefOLDer", false, OneWayStrategy.Mirror);
        assertEquals(OneWayStrategy.MoveFilesInDateFolder, strategyMixed1);
    }


    @Test
    public void OneWayStrategyFromString_Invalid() throws Exception
    {
        final OneWayStrategy strategyInvalid1 = SettingsHelper.OneWayStrategyFromString("Invalid", false, OneWayStrategy.Standard);
        assertEquals(OneWayStrategy.Standard, strategyInvalid1);

        final OneWayStrategy strategyInvalid2 = SettingsHelper.OneWayStrategyFromString("Invalid", false, OneWayStrategy.Mirror);
        assertEquals(OneWayStrategy.Mirror, strategyInvalid2);

        final OneWayStrategy strategyInvalid3 = SettingsHelper.OneWayStrategyFromString("Invalid", false, OneWayStrategy.AllFilesInDateFolder);
        assertEquals(OneWayStrategy.AllFilesInDateFolder, strategyInvalid3);

        final OneWayStrategy strategyInvalid4 = SettingsHelper.OneWayStrategyFromString("Invalid", false, OneWayStrategy.NewFilesInDateFolder);
        assertEquals(OneWayStrategy.NewFilesInDateFolder, strategyInvalid4);

        final OneWayStrategy strategyInvalid5 = SettingsHelper.OneWayStrategyFromString("Invalid", false, OneWayStrategy.MoveFiles);
        assertEquals(OneWayStrategy.MoveFiles, strategyInvalid5);

        final OneWayStrategy strategyInvalid6 = SettingsHelper.OneWayStrategyFromString("Invalid", false, OneWayStrategy.MoveFilesInDateFolder);
        assertEquals(OneWayStrategy.MoveFilesInDateFolder, strategyInvalid6);
    }

    @Test(expected = IllegalArgumentException.class)
    public void OneWayStrategyFromString_Exception() throws Exception
    {
        SettingsHelper.OneWayStrategyFromString("Invalid", true, OneWayStrategy.Standard);
    }


}
